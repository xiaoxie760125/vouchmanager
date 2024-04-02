package com.sqds.vouchdatamanager.Help;

import com.sqds.comutil.RedisUtil;
import com.sqds.ufdataManager.model.ufdata.person;
import com.sqds.ufdataManager.registory.DataDml.personhelp;
import com.sqds.ufdataManager.registory.DataDml.ufdatabasebasic;
import com.sqds.ufdataManager.registory.ufdata.personRepository;
import com.sqds.ufdataManager.registory.ufsystem.Ua_periodRepository;
import com.sqds.vouchdatamanager.model.bankvouchnote;
import com.sqds.vouchdatamanager.model.newsVouchsAllocations;
import com.sqds.vouchdatamanager.model.newsvouchs;
import com.sqds.vouchdatamanager.model.tuiguangfei;
import com.sqds.vouchdatamanager.registroy.bankvouchnoteRepository;
import com.sqds.vouchdatamanager.registroy.newsvouchsmanager;
import com.sqds.vouchdatamanager.registroy.tuiguangfeiRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


/**
 * 发票管理的基本操作
 * 1，增加 更新发票 Insertorupdatevouch
 * 2，发票的查询
 * 3、业绩分单
 * 4.业绩查询
 */
@Transactional
@Service
@Repository
@Aspect
@Slf4j
@EnableAspectJAutoProxy(exposeProxy = true,proxyTargetClass = true)
public class newsvouchsRepositoryHelp implements newsvouchsmanager {
    @PersistenceContext(unitName ="vouchpersistenceunit")
    EntityManager entityManager;
    @Autowired
    tuiguangfeiRepository tuiguangfeiRepository;
    @Autowired
    personRepository ufperson;
    @Autowired
    @Qualifier("ufdatainfo")
    ufdatabasebasic info;
    @Autowired
    Ua_periodRepository periodRepository;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    personhelp perasoninfo;
    private  EntityTransaction transaction=null;






    @Override
    public  person getpersobyname(String personname, String ufpzhangtao,String database)
    {
        this.info.setZhangtaohao(ufpzhangtao);
        Integer year=this.periodRepository.findMaxPeriodByCAcc_id(ufpzhangtao);
        this.info.setYear(year);
        return  this.ufperson.findFirstByCPersonName(personname,info);
    }
    /**
     * 发票订单的更新和增加
     *
     * @param nvouchs
     * @param datasource
     *
     * @return
     */
    @Override
    public Integer Insertorupdatevouchs(List<newsvouchs> nvouchs, String datasource) {

        try {

            //转化人员编码

            /*
            取得报纸发行价格基本配置
             */
            tuiguangfei vm=tuiguangfeiRepository.findByMname(nvouchs.get(0).getMname(), datasource);


            AtomicInteger result = new AtomicInteger();
            AtomicInteger res= new AtomicInteger();

            nvouchs.stream().forEach(s -> {
                Query selectquery = entityManager.createQuery("select v.vouchcode from newsvouchs v where v.vouchcode=:vouchcode");
                selectquery.setParameter("vouchcode", s.getVouchcode());
                List<String> vouchcode = selectquery.getResultList();
                if (vouchcode.size() > 0) {
                    //计算推广费标准

                    BigDecimal[] vp=vm.getPrice()!=0?s.getSum().divideAndRemainder(BigDecimal.valueOf(vm.getPrice())):new BigDecimal[]{s.getSum(),BigDecimal.valueOf(0)};
                    BigDecimal tgvalue=vp[1].compareTo(BigDecimal.valueOf(0))==0?BigDecimal.valueOf(vm.getValue()):BigDecimal.valueOf(0);
                    //根据价格确定保证推广费标准
                    s.setTuiguangfei((vm.getPrice()!=0?new BigDecimal(s.getCount()):s.getSum()).multiply(tgvalue));
                    StringBuilder nativebuild=new StringBuilder();
                    nativebuild.append("update newsvouchs u set u.customername=:customername,u.sum=:sum,u.tuiguangfei=:tuiguanfei," +
                                                            "u.revenue=:revenue,u.vouchtype=:vouchtype,u.shishou=:shishou ");
                    if(s.getUfvouchcode()!=null)nativebuild.append("u.ufvouchcode=:ufvouchcode");
                    if(s.getUftvouchcode()!=null)nativebuild.append(",u.uftvouchcode=:uftvouchcode");
                    nativebuild.append("where u.vouchcode=:vouchcode");
                    Query updatequer = entityManager.createQuery("update newsvouchs u set u.customername=:customername,u.sum=:sum,u.tuiguangfei=:tuiguanfei," +
                    "u.revenue=:revenue,u.vouchtype=:vouchtype,u.shishou=:shishou,u.ufvouchcode=:ufvouchcode where u.vouchcode=:vouchcode")
                            .setParameter("customername", s.getCustomername())
                            .setParameter("tuiguanfei", s.getTuiguangfei())
                            .setParameter("revenue", s.getRevenue())
                            .setParameter("vouchtype", s.getVouchtype())
                            .setParameter("sum", s.getSum())
                            .setParameter("shishou", s.getShishou()).setParameter("ufvouchcode", s.getUfvouchcode()).setParameter("vouchcode", s.getVouchcode());
                    //生產推廣非訂單

                    result.addAndGet(updatequer.executeUpdate());
                    if (s.getShishou().compareTo(new BigDecimal(0)) > 0) {
                        String nvouchcode = s.getVouchcode() + "01";

                        Query code = entityManager.createQuery("select u.allcode from newsVouchsAllocations u where u.allcode=:vouchcode")
                                .setParameter("vouchcode", nvouchcode);
                        if (code.getResultList()== null || code.getResultList().size()==0) {
                            newsVouchsAllocations newsVouchsAllocations = new newsVouchsAllocations();
                            newsVouchsAllocations.setAllcode(s.getVouchcode() + "01");
                            newsVouchsAllocations.setCount(s.getCount());
                            newsVouchsAllocations.setIsend(false);
                            newsVouchsAllocations.setIsorpsn(true);
                            newsVouchsAllocations.setCpsn_num(s.getCpsn_num());
                            newsVouchsAllocations.setShishou(s.getShishou());
                            newsVouchsAllocations.setTuiguanfei(s.getTuiguangfei());
                            entityManager.persist(newsVouchsAllocations);
                        }
                        else
                        {
                            //多分单逻辑
                            Query updatequery=entityManager.createQuery("update " +
                                    "newsVouchsAllocations  va set va.shishou=:shishou*va.count,va.tuiguanfei=:tuiguanfei*va.count where va.newsvouchs=:nesvouch "
                            );
                            newsVouchsAllocations con=new newsVouchsAllocations();
                            updatequery.setParameter("shishou", s.getShishou().divide(BigDecimal.valueOf(s.getCount())));
                            updatequery.setParameter("tuiguanfei", s.getTuiguangfei().divide(BigDecimal.valueOf(s.getCount())).setScale(2, RoundingMode.HALF_UP));
                            updatequery.setParameter("nesvouch",s);
                            result.addAndGet(updatequery.executeUpdate());
                            //newsVouchsAllocations nupall=(newsVouchsAllocations) code.getSingleResult();

                        }
                    }

                }
                else {
                     

                       s.setShishou(BigDecimal.valueOf(0));
                       newsVouchsAllocations sc=new newsVouchsAllocations();
                       //单独有价格的逻辑
                       //if(vm.getPrice()!=0)
                       {

                       
                        BigDecimal[] c =vm.getPrice()!=0?s.getSum().divideAndRemainder(BigDecimal.valueOf(vm.getPrice())):new BigDecimal[]{s.getSum(),BigDecimal.ZERO};                  
                                     /*
                                   生成价格判断与份数生成逻辑
                                    */
                        if (c[1].compareTo(BigDecimal.ZERO) == 0) {
                           if(Math.abs(vm.getValue())>1){s.setCount(c[0].intValue());}
                            s.setTuiguangfei(BigDecimal.valueOf(vm.getValue()*c[0].intValue()));
                        } else {
                            BigDecimal[] b = s.getSum().divideAndRemainder(BigDecimal.valueOf(vm.getPrice() - vm.getValue()));
                            if (b[1].compareTo(BigDecimal.ZERO) == 0) {
                                s.setCount(b[0].intValue());
                                s.setTuiguangfei(BigDecimal.ZERO);
                            } else {
                                throw new RuntimeException("发行价格不符合发行政策");
                            }
                        }                        
                    }
                    sc.setAllcode(s.getVouchcode()+"01");
                    sc.setIsend(false);
                    sc.setCount(s.getCount());
                    sc.setIsorpsn(true);
                    sc.setCpsn_num(s.getCpsn_num());
                   // sc.setAwdcode(s.getVouchcode());
                    sc.setShishou(BigDecimal.valueOf(0));
                    sc.setTuiguanfei(BigDecimal.valueOf(0));
                    sc.setNewsvouchs(s);
                        entityManager.persist(sc);
                        entityManager.persist(s);

                }

            });
           entityManager.flush();
           this.entityManager.clear();

            return result.get();
        }
        catch (Exception e)
        {

            return  0;
        }

        }

    /**
     * 取消收款结算的逻辑 2024年1月14日 谢志刚
     * @param needcanclevouchs
     * @param year
     * @return
     */
    @Override
    public Integer cancle(List<newsvouchs> needcanclevouchs, String year,boolean iscancleshoukuan) {
        try {
            //从结算订单号得到
            List<String> vouchcodes = needcanclevouchs.stream().map(s -> s.getVouchcode()
            ).collect(Collectors.toList());
            Query getneedupdate = this.entityManager.createQuery("select u from newsVouchsAllocations  u where u.allcode in :vouchcode").setParameter("vouchcode",vouchcodes);
            List<newsVouchsAllocations> newsVouchsAllocations = getneedupdate.getResultList();
            for (newsVouchsAllocations np : newsVouchsAllocations) {
                if(iscancleshoukuan) {
                    BigDecimal updatevouch = np.getNewsvouchs().getShishou().subtract(np.getShishou()).intValue() >= 0 ? np.getNewsvouchs().getShishou().subtract(np.getShishou()) : BigDecimal.valueOf(0);
                    np.getNewsvouchs().setShishou(updatevouch);
                    //更改凭证号的状态
                    np.getNewsvouchs().setUfvouchcode(np.getUfvouchcode()+"(已取消)");


                }
                else
                {
                    np.getNewsvouchs().setUftvouchcode(np.getNewsvouchs().getUftvouchcode()+"(已取消)");
                    np.setIsend(false);
                }
                this.entityManager.merge(np);
            }
            this.entityManager.flush();
            entityManager.clear();
            return  200;
        }
        catch (Exception e)
        {
            return  500;
        }
    }

    List<person> udataperson=new ArrayList<>();
        private  person GetPersonFromPsn_num(String psn_num,String zhangtaohao) {
            if (info.getZhangtaohao()==null || !info.getZhangtaohao().equals(zhangtaohao)) {
                    this.info.setZhangtaohao(zhangtaohao);
                    int maxperiod = this.periodRepository.findMaxPeriodByCAcc_id(zhangtaohao);
                    info.setYear(maxperiod);
                }



                return this.ufperson.findFirstByCPersonCode(psn_num, info);

        }

     public List<person> getUdataperson(String zhangtaohao)
     {
       /*  List<person> needperson=new ArrayList<>();
         if (info.getZhangtaohao()==null || !info.getZhangtaohao().equals(zhangtaohao)) {
             info.setZhangtaohao(zhangtaohao);
             int maxperiod = this.periodRepository.findMaxPeriodByCAcc_id(zhangtaohao);
             info.setYear(maxperiod);
         }
         this.perasoninfo=new personhelp();*/
         //从redis获取人员信息
        return  this.perasoninfo.GetPersonFromPsn_num(zhangtaohao);
     }
    @Override
    public List<newsvouchs> getvouchde(vouchcontion vouchcontion,String datasource) {

         //多订单编号查询

        //减少数据库查询人员信息次数
        List<person> needperson=new ArrayList<>();
        needperson=getUdataperson(vouchcontion.getUfpzhangtao());

        String sql="select vouchcode,count,mname," +
                "ns.cpsn_num,ufzhangtao,ufvouchcode"+
                ",shishou,tuiguanfei,vouchtype,vouchid,customername,vouchdate,revenue," +
                "tax,sum,kaipiaoren,bankid,ufvouchcode,uftvouchcode,isadvances"
                +"  from newsvouchs  ns  "
               +" where 1=1 ";
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
        //DateFormat formatter=DateFormat.getDateInstance();
       if(vouchcontion.getVouchcode()!=null&&!vouchcontion.getVouchcode().equals("")){

                sql += " and substring(vouchcode,len(vouchcode)-7,8) in (" + vouchcontion.getVouchcode() + ") ";

        }

        sql+="and vouchdate between '"+formatter.format(vouchcontion.getBegindate())+
                "' and '"+formatter.format(vouchcontion.getEnddate())+"'";
        if(vouchcontion.getMname()!=null && !vouchcontion.getMname().isEmpty())
        {
            sql+=" and mname='"+vouchcontion.getMname()+"'";
        }
        if(vouchcontion.getCustomername()!=null&&vouchcontion.getCustomername()!="")
        {
            sql+=" and customername like '%"+vouchcontion.getCustomername()+"%'";
        }
        if(vouchcontion.getCpsn_num()!=null && !(vouchcontion.getCpsn_num() ==""))
        {
            sql+="  and ns.cpsn_num='"+vouchcontion.getCpsn_num()+"'";
        }
        //后端增加查询查询筛选逻辑
        if(vouchcontion.isIsshoukuan())
        {
            sql+=" and shishou<sum";
        }
        if(vouchcontion.isIsyeji())
        {
            sql+=" and shishou>0";
        }
        Query decselect=entityManager.createNativeQuery(sql);
        List<Object> rows=  decselect.getResultList();
        List<newsvouchs> result=new ArrayList<newsvouchs>();
        int sumcount=0;
        BigDecimal sumshishou=BigDecimal.valueOf(0);
        BigDecimal sumstax=BigDecimal.valueOf(0);
        BigDecimal sum=BigDecimal.valueOf(0);
        BigDecimal sumtuiguangfei=BigDecimal.valueOf(0);
        BigDecimal sumrevenfei=BigDecimal.valueOf(0);
        for(Object vt:rows) {
                Object[] obj = (Object[]) vt;
                newsvouchs nesvouchs = new newsvouchs();
                nesvouchs.setVouchcode(obj[0]!=null?obj[0].toString():"");
                nesvouchs.setCount(obj[1]!=null?Integer.parseInt(obj[1].toString()):0);
                sumcount+=(obj[1]!=null?Integer.parseInt(obj[1].toString()):0);
                nesvouchs.setMname(obj[2]!=null?obj[2].toString():"");
                nesvouchs.setCpsn_num(obj[3]!=null?obj[3].toString():"");
                nesvouchs.setUfzhangtao(obj[4]!=null?obj[4].toString():"");
                nesvouchs.setUfvouchcode(obj[5]!=null?obj[5].toString():"");
                nesvouchs.setShishou(obj[6]!=null?(BigDecimal) obj[6]:BigDecimal.valueOf(0));
                sumshishou=sumshishou.add(obj[6]!=null?(BigDecimal) obj[6]:BigDecimal.valueOf(0));
                nesvouchs.setTuiguangfei(obj[7]!=null?(BigDecimal) obj[7]:BigDecimal.valueOf(0));
                sumtuiguangfei=sumtuiguangfei.add(obj[7]!=null?(BigDecimal) obj[7]:BigDecimal.valueOf(0));
                nesvouchs.setVouchtype(obj[8]!=null?obj[8].toString():"");
                nesvouchs.setVouchid(obj[9]!=null?obj[9].toString():"");
                nesvouchs.setCustomername(obj[10]!=null?obj[10].toString():"");
                nesvouchs.setVouchdate(obj[11]!=null?((Date) obj[11]):new Date());
                nesvouchs.setRevenue(obj[12]!=null?(BigDecimal) obj[12]:BigDecimal.valueOf(0));
                sumrevenfei=sumrevenfei.add(obj[12]!=null?(BigDecimal) obj[12]:BigDecimal.valueOf(0));
                nesvouchs.setTax(obj[13]!=null?(BigDecimal) obj[13]:BigDecimal.valueOf(0));
                sumstax=sumstax.add(obj[13]!=null?(BigDecimal) obj[13]:BigDecimal.valueOf(0));
                nesvouchs.setSum(obj[14]!=null?(BigDecimal) obj[14]:BigDecimal.valueOf(0));
                sum=sum.add(obj[14]!=null?(BigDecimal) obj[14]:BigDecimal.valueOf(0));
                nesvouchs.setKaipiaoren(obj[15]!=null?obj[15].toString():"");
                nesvouchs.setBankid(obj[16]!=null?obj[16].toString():"");
                nesvouchs.setUftvouchcode(obj[18]!=null?obj[18].toString():"");
                nesvouchs.setProvouchcode(obj[18]!=null?obj[18].toString():"");
                nesvouchs.setIsadvances(obj[19]!=null?(Boolean) obj[19]:false);
                Optional<person> orgperson =needperson.stream().filter(s -> s.getCPsn_Num().equals(nesvouchs.getCpsn_num())).findFirst();
                if(orgperson.isPresent())
                {
                    nesvouchs.setUfperson(orgperson.get());
                }
                else {  try {
                    person p = GetPersonFromPsn_num(nesvouchs.getCpsn_num(), vouchcontion.getUfpzhangtao());
                       if(p!=null) {
                           nesvouchs.setUfperson(p);
                           needperson.add(p);
                       }
                    }
                    catch (Exception E)
                    {
                        System.out.println(E.getMessage());
                    }

                }
                result.add(nesvouchs);

        }
        newsvouchs vouchsum=new newsvouchs();
        vouchsum.setVouchcode("合计");
        vouchsum.setCount(sumcount);
        vouchsum.setSum(sum);
        vouchsum.setShishou(sumshishou);
        vouchsum.setTuiguangfei(sumtuiguangfei);
        vouchsum.setTax(sumstax);
        vouchsum.setRevenue(sumrevenfei);
        result.add(vouchsum);




        return result;




    }

    /**
     *
     * 职工业绩表
     * @param datasource
     * @param vouchcontion
     * @return
     */
    @Override
    public List<newsvouchs> getvouchallocation(vouchcontion vouchcontion,String datasource) {

        try {List<person> needperson = new ArrayList<>();
            if (needperson.isEmpty()) {
                needperson = getUdataperson(vouchcontion.getUfpzhangtao());
            }
            String[] vouchcode;
            if(vouchcontion.getVouchcode()!=null) {
                vouchcode = vouchcontion.getVouchcode().replace("'", "").split(",");
            } else {
                vouchcode = null;
            }
            Query query = this.entityManager.createQuery("select vouch,nd from  newsvouchs vouch join newsVouchsAllocations  nd  " +
                    "  on vouch=nd.newsvouchs  where vouch.shishou>0  " +
                    "  and (vouch.customername like concat('%',:customer,'%') or :customer is null)" +
                    "  and (nd.cpsn_num=:cpsnname or :cpsnname is null) " +
                    "  and (nd.isend=:isyeji)" +
                    "  and vouch.mname=:mname " +
                    "  and vouch.vouchdate between :begindate and :enddate");

            query.setParameter("customer", vouchcontion.getCustomername());
            query.setParameter("begindate", vouchcontion.getBegindate());
            query.setParameter("enddate", vouchcontion.getEnddate());
            query.setParameter("cpsnname", vouchcontion.getCpsn_num());
            query.setParameter("isyeji", !vouchcontion.isIsyeji());
            query.setParameter("mname", vouchcontion.getMname());

            List<Object[]> list = query.getResultList();
           /* if(vouchcode!=null)
            {
                list=list.stream().filter(s-> {
                newsvouchs vd=(newsvouchs)s[0];
                return Arrays.stream(vouchcode).anyMatch(b->b.equals(vd.getVouchcode()));
                }).toList();
            }*/
            List<newsvouchs> newsvouchs = new ArrayList<>();
            int sumcount = 0;
            BigDecimal sum = new BigDecimal(0);
            BigDecimal sumvc = new BigDecimal(0);
            BigDecimal sumtax = new BigDecimal(0);
            BigDecimal shishou = new BigDecimal(0);
            BigDecimal sumtuiguangfei=new BigDecimal(0);
            for (Object[] objects : list) {
                newsvouchs v = (newsvouchs) objects[0];
                newsVouchsAllocations vc = (newsVouchsAllocations) objects[1];
                newsvouchs res = new newsvouchs();
                ///业绩单号 2023年12月1日
                res.setVouchcode(vc.getAllcode());
                res.setSum(v.getSum());
                sumcount += vc.getCount();
                sum = sum.add(v.getSum()==null?BigDecimal.valueOf(0):v.getSum());
                sumvc = sumvc.add(v.getRevenue()==null?BigDecimal.valueOf(0):v.getRevenue());
                sumtax = sumtax.add(v.getTax()==null?BigDecimal.valueOf(0):v.getTax());
                shishou = shishou.add(vc.getShishou()==null?BigDecimal.valueOf(0):vc.getShishou());
                sumtuiguangfei=sumtuiguangfei.add(vc.getTuiguanfei()==null?BigDecimal.valueOf(0):vc.getTuiguanfei());


                res.setTax(v.getTax());
                res.setRevenue(v.getRevenue());
                res.setVouchtype(v.getVouchtype());
                res.setCount(vc.getCount());
                res.setShishou(vc.getShishou());
                res.setTuiguangfei(vc.getTuiguanfei());
                res.setVouchdate(v.getVouchdate());
                res.setMname(v.getMname());
                res.setUfvouchcode(v.getUfvouchcode());
                res.setUftvouchcode(v.getUftvouchcode());
                res.setCpsn_num(vc.getCpsn_num());
                res.setCustomername(v.getCustomername());
                //添加识别是否分单逻辑 2024年2月22日
                res.setIsadvances(!vc.isIsorpsn());
                person p = needperson.stream().filter(s -> s.getCPsn_Num().equals(res.getCpsn_num())).findFirst().orElse(null);
                if (p != null) {
                    res.setUfperson(p);
                } else {
                    p = GetPersonFromPsn_num(v.getCpsn_num(), vouchcontion.getUfpzhangtao());
                    if(p!=null) {
                        v.setUfperson(p);
                        needperson.add(p);
                    }
                }


                newsvouchs.add(res);
            }
            newsvouchs sumnews = new newsvouchs();
            sumnews.setVouchcode("合计");
            sumnews.setSum(sum);
            sumnews.setTax(sumtax);
            sumnews.setShishou(shishou);
            sumnews.setRevenue(sumvc);sumnews.setCount(sumcount);
            sumnews.setTuiguangfei(sumtuiguangfei);
            ;
            newsvouchs.add(sumnews);

            return newsvouchs;
        }
        catch (Exception e)
        {
            return  null;
        }
    }

    /**
     * 发票业绩分单
     * @param datasource
     * @param newsVouchAllocations
     * @param cpsn_num
     * @param count
     * @return
     */
    @Override
    @Transactional
    public Integer allocation(List<newsVouchsAllocations> newsVouchAllocations, String cpsn_num, Integer count,String datasource) {

        final Integer[] result = {0};
        try {

            newsVouchAllocations.stream().forEach(s->{
                    Query allcode = entityManager.createQuery("" +
                            "select v from newsVouchsAllocations  v where v.allcode=:vouchode and  v.isorpsn=true");
                    allcode.setParameter("vouchode", s.getAllcode());
                    if (allcode.getResultList() != null && allcode.getResultList().size() != 0)
                    {   newsVouchsAllocations nv = (newsVouchsAllocations) allcode.getSingleResult();
                        if (nv.getCount() < count) {
                            throw new RuntimeException("分单数量不能超过原单数量");
                        }
                        ///业绩全转
                        if (nv.getCount() == count) {
                            nv.setCpsn_num(cpsn_num);
                            nv.setIsorpsn(false);
                            entityManager.merge(nv);
                            result[0]++;
                        }
                        /*
                         *分单
                         */
                        else {
                            Query newsvouchs = entityManager.createQuery(
                                    "select v from  newsVouchsAllocations  v where v.newsvouchs=:vouchcode order by v.allcode");
                            newsvouchs.setParameter("vouchcode", nv.getNewsvouchs());
                            Integer nextindex = 2;
                            if (newsvouchs.getResultList() != null && newsvouchs.getResultList().size() != 0) {
                                var a=newsvouchs.getResultList();
                                newsVouchsAllocations vlast = (newsVouchsAllocations) a.toArray()[a.size()-1];
                                nextindex = Integer.parseInt(vlast.getAllcode().substring(vlast.getAllcode().length()-2, vlast.getAllcode().length())) + 1;
                            }
                            String nvcode = nv.getNewsvouchs().getVouchcode() + (nextindex > 9 ? nextindex : "0" + nextindex);
                            nv.setShishou(nv.getShishou().divide(new BigDecimal(nv.getCount())).multiply(new BigDecimal(nv.getCount() - count)));
                            nv.setTuiguanfei(nv.getTuiguanfei().divide(new BigDecimal(nv.getCount())).multiply(new BigDecimal(nv.getCount() - count)));
                            nv.setCount(nv.getCount() - count);
                            entityManager.merge(nv);
                            result[0]++;
                            newsVouchsAllocations nv2 = new newsVouchsAllocations();
                            nv2.setAllcode(nvcode);
                            nv2.setCpsn_num(cpsn_num);
                            nv2.setCount(count);
                            nv2.setIsorpsn(false);
                            nv2.setShishou(nv.getShishou().divide(new BigDecimal(nv.getCount())).multiply(new BigDecimal(count)));
                            nv2.setTuiguanfei(nv.getTuiguanfei().divide(new BigDecimal(nv.getCount())).multiply(new BigDecimal(count)));
                            nv2.setNewsvouchs(nv.getNewsvouchs());
                            entityManager.persist(nv2);
                            result[0]++;
                        } }
            });

           this.entityManager.flush();
           this.entityManager.clear();

        }
        catch (Exception E)
        {

             System.out.println(E.getMessage());
        }

        return result[0];
    }

    ///业绩
    @Override
    public List<newsvouchs> allocationend(List<newsvouchs> needall, String datasource) {
        List<String> needuploadcode=needall.stream().map(newsvouchs::getVouchcode).collect(Collectors.toList());
        //更新业绩结算数据款
        needall.forEach(s-> {
                 System.out.println( s.getVouchcode().substring(0,s.getVouchcode().length()-2));
                    Query updatevouch = this.entityManager.createQuery("" +
                                    "update newsvouchs  v set v.uftvouchcode=:ufvouchcode  where v.vouchcode=:vcode")
                            .setParameter("ufvouchcode", s.getUftvouchcode())
                            .setParameter("vcode", s.getVouchcode().substring(0,s.getVouchcode().length()-2));
                    updatevouch.executeUpdate();

            Query allend= this.entityManager.createQuery("" +
                    "update newsVouchsAllocations all set all.isend=true,all.enddate=:enddate where " +
                    "allcode=:needuploadcode ");
            allend.setParameter("needuploadcode",s.getVouchcode());
            allend.setParameter("enddate",new Date());
            allend.executeUpdate();


                }

        );


        this.entityManager.flush();
        this.entityManager.clear();
        return  needall;
    }

    @Override
    public List<newsvouchs> fendanlist(vouchcontion vouchcontion, String datasource)
    {
        List<person> needperson=this.getUdataperson(vouchcontion.getUfpzhangtao());
        Query fendan=this.entityManager.createQuery(
                " select v,vn from  newsvouchs  v " +
                        "join newsVouchsAllocations vn on v=vn.newsvouchs where v.shishou<v.sum and  " +
                        " (v.customername like concat('%',:cumstomer,'%') or :cumstomer is null )" +
                        " and (v.vouchcode=:vouchcode or :vouchcode is null ) " +
                        " and (v.cpsn_num=:cpsn_num or :cpsn_num is null) " +
                        " and (v.vouchdate between  :begindate and :enddate)" +
                        " and vn.isend=false  and vn.isorpsn=true " +
                        " and v.mname=:vmname");
          fendan.setParameter("cumstomer",vouchcontion.getCustomername());
          fendan.setParameter("vouchcode",vouchcontion.getVouchcode());
          fendan.setParameter("cpsn_num",vouchcontion.getCpsn_num());
          fendan.setParameter("begindate",vouchcontion.getBegindate());
          fendan.setParameter("enddate",vouchcontion.getEnddate());
          fendan.setParameter("vmname",vouchcontion.getMname());
          List<Object[]> list=fendan.getResultList();
        int sumcount=0;
        BigDecimal sum=new BigDecimal(0);
        BigDecimal sumvc=new BigDecimal(0);
        BigDecimal sumtax=new BigDecimal(0);
        BigDecimal shishou=new BigDecimal(0);
        List<newsvouchs> newsvouchs=new ArrayList<>();
        for (Object[] objects : list) {
            newsvouchs v=(newsvouchs)objects[0];
            newsVouchsAllocations vc=(newsVouchsAllocations)objects[1];
            newsvouchs res=new newsvouchs();
            ///分单单号 2023年12月9日
            res.setVouchcode(v.getVouchcode());
            res.setSum(v.getSum());
            sumcount+=vc.getCount();
            sum=sum.add(v.getSum());
            sumvc=sumvc.add(v.getRevenue());
            sumtax=sumtax.add(v.getTax());
            shishou=shishou.add(v.getShishou());


            res.setTax(v.getTax());
            res.setRevenue(v.getRevenue());
            res.setVouchtype(v.getVouchtype());
            res.setCount(vc.getCount());
            res.setShishou(vc.getShishou());
            res.setTuiguangfei(vc.getTuiguanfei());
            res.setVouchdate(v.getVouchdate());
            res.setMname(v.getMname());
            res.setUfvouchcode(v.getUfvouchcode());
            res.setUftvouchcode(v.getUftvouchcode());
            res.setCpsn_num(vc.getCpsn_num());
            res.setCustomername(v.getCustomername());
            person p=needperson.stream().filter(s->s.getCPsn_Num().equals(res.getCpsn_num())).findFirst().orElse(null);
            if (p!=null)
            {
                res.setUfperson(p);
            }
            else {
                res.setUfperson(ufperson.findFirstByCPersonCode(res.getCpsn_num(), info));
            }


            newsvouchs.add(res);
        }
        newsvouchs sumnews=new newsvouchs();
        sumnews.setVouchcode("合计");
        sumnews.setSum(sum);
        sumnews.setTax(sumtax);
        sumnews.setShishou(shishou);
        sumnews.setRevenue(sumvc);
        sumnews.setCount(sumcount);;
        newsvouchs.add(sumnews);

        return newsvouchs;

    }

    /**
     * 分单的逻辑，一份多
     *
     * @param needall
     * @param needperson
     * @param datasource
     * @return
     */

    //分单操作
    @Override
    public String fendan(newsvouchs needall, Map<String, Integer> needperson, String datasource) {
      try {
          String vouchcode=needall.getVouchcode();
          Query selectquery = entityManager.createQuery("select vc  from newsVouchsAllocations vc " +
                  " where vc.newsvouchs=:v ");
          selectquery.setParameter("v", needall);
          List<newsVouchsAllocations> vsn = (List<newsVouchsAllocations>) selectquery.getResultList();
          int sum = needperson.values().stream().reduce(0, (sumv, sv) -> sumv + sv);
          newsVouchsAllocations v=(newsVouchsAllocations)vsn.stream().filter(s->s.getCpsn_num().equals(needall.getCpsn_num())).findFirst().orElse(null);
          if (v.getCount() < sum) {
              throw new RuntimeException("分单数超超过限额");
          }
          if(v!=null)
          {
              v.setCount(v.getCount()-sum);
              entityManager.merge(v);
          }


          final int[] idex = new int[1];
          needperson.forEach((k, vs) -> {

             String voucccode=vouchcode+k;
              newsVouchsAllocations needc = vsn.stream().filter(s -> s.getAllcode().equals(voucccode)).findFirst().orElse(null);
              //全转逻辑

              if (needc != null) {
                  needc.setCount(needc.getCount() + vs);

              } else {

                  needc = new newsVouchsAllocations();
                  needc.setNewsvouchs(needall);
                  needc.setAllcode(voucccode);
                  needc.setCpsn_num(k);
                  needc.setCount(vs);
                  entityManager.persist(needc);
                  idex[0]++;
              }
          });
          this.entityManager.flush();
          this.entityManager.clear();
          this.entityManager.close();
          return  "数据已保存";
      }
      catch (RuntimeException exception)
      {
          return  exception.getMessage();
      }






    }

    @Override
    public List<newsvouchs> sumyejibyperson(vouchcontion vouchcontion, String datasource) {
       List<person> personList=getUdataperson(vouchcontion.getUfpzhangtao());
        Query  databasic= this.entityManager.createQuery(
                "select v.cpsn_num  as cpsn_num,sum(v.count) as count ,sum(v.shishou) as shishou,sum(v.tuiguanfei) " +
                         "  from newsVouchsAllocations  v " +
                        "join newsvouchs  vs on v.newsvouchs=vs " +
                        " where  vs.vouchdate between  :begindate and  :enddate and v.shishou>0" +
                        " and (v.cpsn_num=:cpsn_num  or :cpsn_num is null) " +
                        " and vs.mname=:mname "+
                        " group by  v.cpsn_num order by  shishou desc "
        ).setParameter("begindate",vouchcontion.getBegindate())
                .setParameter("enddate",vouchcontion.getEnddate()).setParameter("cpsn_num", vouchcontion.getCpsn_num()).setParameter("mname",vouchcontion.getMname());
     List result=databasic.getResultList();
      List<newsvouchs> vn=new ArrayList<>();
        Integer sumcount=0;
        BigDecimal sumshishou=BigDecimal.valueOf(0);
        BigDecimal sumtuiguangfei=BigDecimal.valueOf(0);

      for(Object o:result)
      {
          Object[] obj=(Object[])o;
          newsvouchs newsvouchs=new newsvouchs();
          person p=personList.stream().filter(s->s.getCPsn_Num().equals(obj[0].toString())).findFirst().orElse(null);

          if(p!=null)
          {
              newsvouchs.setUfperson(p);
              newsvouchs.setCustomername(p.getCDept_num());
          }
          else
          {
              info.setZhangtaohao(vouchcontion.getUfpzhangtao());
              info.setYear(this.periodRepository.findMaxPeriodByCAcc_id(vouchcontion.getUfpzhangtao()));
              p=ufperson.findFirstByCPersonCode(obj[0].toString(),info);
              newsvouchs.setUfperson(p);
              if(p!=null)newsvouchs.setCustomername(p.getCDept_num());
          }
          newsvouchs.setCpsn_num((String)obj[0]);
          if(obj[1] instanceof  Long)
          {
              newsvouchs.setCount(((Long)obj[1]).intValue());
          }
          newsvouchs.setShishou(obj[2]==null?BigDecimal.valueOf(0):(BigDecimal)obj[2]);
          newsvouchs.setTuiguangfei(obj[3]==null?BigDecimal.valueOf(0):(BigDecimal)obj[3]);
          sumshishou=sumshishou.add(newsvouchs.getShishou());
          sumtuiguangfei=sumtuiguangfei.add(newsvouchs.getTuiguangfei());
          sumcount+=newsvouchs.getCount();
          vn.add(newsvouchs);


      }
   /*  *//* vn=vn.stream().sorted((s,v)->{
          if(s.getCustomername()!=null && v.getCustomername()!=null) {
              return Integer.parseInt(s.getCustomername()) - Integer.parseInt(v.getCustomername());
          }
          return  0;*//*
      }).toList();*/
      newsvouchs sum=new newsvouchs();
      sum.setCount(sumcount);
      sum.setShishou(sumshishou);
      sum.setTuiguangfei(sumtuiguangfei);
      sum.setCustomername("2000");
      vn.add(sum);



        return  vn;

    }

    @Override
    public Map sumtext(LocalDateTime begindate, LocalDateTime enddate, String database) {
        return null;
    }


    /**
     * 汇总数据
     * @param begindate
     * @param enddate
     * @return
     */
    @Override
    public Map<String,Object> sumtext(LocalDateTime begindate, LocalDateTime enddate, String manem, String database) {
       try {


           Query query = this.entityManager.createQuery("" +
                   "select sum(v.count),sum(v.sum),sum(v.shishou) from  newsvouchs  v" +
                   " where  v.vouchdate between :begindate and :enddate and  mname=:mname").setParameter(
                   "begindate", begindate).setParameter("enddate", enddate).setParameter("mname", manem);
           Object[] sum = (Object[]) query.getSingleResult();
           Map<String, Object> map = new HashMap<>();

           map.put("count", Integer.parseInt(sum[0].toString()));
           map.put("sum", (BigDecimal) sum[1]);
           map.put("shishou", (BigDecimal) sum[2]);
           // map.put("shishoucount",(BigDecimal.valueOf(Integer.parseInt(sum[0].toString()))).multiply((BigDecimal)sum[2]).divide((BigDecimal)sum[1]));
           map.put("revenue", ((BigDecimal) sum[1]).subtract((BigDecimal) sum[2]));

           map.put("percentage", ((BigDecimal) sum[1]).intValue() > 0 ? ((BigDecimal) sum[2]).divide((BigDecimal) sum[1], BigDecimal.ROUND_HALF_UP) : BigDecimal.valueOf(0));


           return map;
       }
       catch (Exception E)
       {
           log.error(E.getMessage());
           return null;
       }
    }

    /**
     * 开票总量票据汇总表
     * @param vouchcontion
     * @param database
     * @return
     */
    public  List<newsvouchs> vouchsum(vouchcontion vouchcontion,String database)
    {
       List<person> persnresult=this.getUdataperson(vouchcontion.getUfpzhangtao());

        Query sumbyperson=this.entityManager.createQuery(
                "select v.cpsn_num,sum(v.count),sum(v.sum),sum(v.shishou) from  newsvouchs  v" +
                        " where  v.vouchdate between :begindate and :enddate " +
                        " and (v.cpsn_num=:cpsn_num  or :cpsn_num is null) " +
                        " and v.mname=:mname"+
                        " group by v.cpsn_num order by  sum(v.count) desc,sum(v.shishou) desc").setParameter("begindate",vouchcontion.getBegindate())
                .setParameter("enddate",vouchcontion.getEnddate()).setParameter("cpsn_num",vouchcontion.getCpsn_num()).setParameter("mname",vouchcontion.getMname());

        List<Object[]> sumbypersonlist=sumbyperson.getResultList();
        List<newsvouchs> vsresult=new ArrayList<>();
        Integer sumcount=0;
        BigDecimal sum=new BigDecimal(0);
        BigDecimal shishou=new BigDecimal(0);

        for (Object[] objects : sumbypersonlist)
        {
            newsvouchs vs=new newsvouchs();
            person ps=persnresult.stream().filter(s->s.getCPsn_Num().equals(objects[0].toString())).findFirst().orElse(null);
            if(ps==null)
            {
                info.setZhangtaohao(vouchcontion.getUfpzhangtao());
                info.setYear(this.periodRepository.findMaxPeriodByCAcc_id(vouchcontion.getUfpzhangtao()));

                ps=ufperson.findFirstByCPersonCode(objects[0].toString(),info);
            }
            vs.setUfperson(ps);
            if(objects[1] instanceof  Long)
            {
                vs.setCount(((Long) objects[1]).intValue());
            }

            vs.setSum((BigDecimal)objects[2]);
            vs.setShishou((BigDecimal)objects[3]);
            sumcount+=vs.getCount();
            sum=sum.add(vs.getSum()==null?BigDecimal.valueOf(0):vs.getSum());
            shishou=shishou.add(vs.getShishou()==null?BigDecimal.valueOf(0):vs.getShishou());
            vs.setPercentage(vs.getShishou().intValue()==0?BigDecimal.valueOf(0):vs.getShishou().divide(vs.getSum(),2,BigDecimal.ROUND_HALF_UP));

           vsresult.add(vs);

        }
        newsvouchs vssum=new newsvouchs();
        vssum.setSum(sum);
        vssum.setCount(sumcount);
        vssum.setShishou(shishou);
        vssum.setPercentage(vssum.getShishou().intValue() ==0?BigDecimal.valueOf(0):vssum.getShishou().divide(vssum.getSum(),2, RoundingMode.UP));
        vsresult.add(vssum);

        return  vsresult;
    }
    @Autowired
    bankvouchnoteRepository banknnote;
    @Override
    public int addbannode(List<newsvouchs> node,String cdigest, String database) {
        try {
            SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
            String[] info=cdigest.split("\\|");
           formatter.format(new Date());
            bankvouchnote bankv = new bankvouchnote();
            bankv.setMname(info[1]);
            bankv.setCdigest(node.get(0).getCustomername() + (node.size() > 1 ? ("等" + node.size() + "家") : "") + info[0]);
            bankv.setBankcode(node.get(0).getUfperson().getCPsnAccount());
            bankv.setBilldate(new Date());
            BigDecimal sumtuiguanfei = new BigDecimal(0);
           // Query insertdata=this.entityManager.createNativeQuery("insert into  bankvouchnote(mname,cdigest,bankcode,billdate,[values],fdep) VALUES(?,?,?,?,?,?)");
            for (var n : node) {
                sumtuiguanfei = sumtuiguanfei.add(n.getTuiguangfei());
            }
            bankv.setValues(sumtuiguanfei.doubleValue());
            this.entityManager.persist(bankv);
            this.entityManager.flush();
            this.entityManager.clear();

            return  1;
        }
        catch (Exception e)
        {
            return 0;
        }


    }


    private String  grenatecode(int count)
    {
        if(count==0)return "01";
        return count<10?"0"+count:count+"";

    }
    public void test1() {

    }
}
