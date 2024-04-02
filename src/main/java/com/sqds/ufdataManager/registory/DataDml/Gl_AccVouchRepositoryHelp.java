package com.sqds.ufdataManager.registory.DataDml;

import com.sqds.comutil.ToolUtil;
import com.sqds.ufdataManager.model.ufdata.GL_AccSum;
import com.sqds.ufdataManager.model.ufdata.Gl_Accvouch;
import com.sqds.ufdataManager.model.ufdata.Gl_accass;
import com.sqds.ufdataManager.model.ufdata.code;
import com.sqds.ufdataManager.registory.ufdata.CustomerVouchManager;
import com.sqds.ufdataManager.registory.ufdata.Gl_mendRepository;
import com.sqds.ufdataManager.registory.ufdata.codeRepository;
import com.sqds.ufdataManager.registory.ufdata.vouchcontioan;
import com.sqds.ufdataManager.registory.ufsystem.Ua_periodRepository;
import com.sqds.vouchdatamanager.model.vouchmanager;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Repository
public class Gl_AccVouchRepositoryHelp  implements CustomerVouchManager {
    @Autowired
    private Ua_periodRepository uaPeriodRepository;
    @Autowired
    private Gl_mendRepository glMendRepository;
    @Autowired
    @Qualifier("ufdataDataSource")
    private DataSource dataSource;
    @Autowired
    private codeRepository codeRepository;
    @PersistenceUnit(unitName = "ufdata")
   EntityManagerFactory ufdataEntityManagerfactory;



    /**
     * 生成单个凭证
     * @param info
     * @param vouchs
     * @return
     */
    @Override
    @Modifying
    @Transactional
    public String Insertvouch(@Qualifier("ufdatainfo") ufdatabasebasic info, List<Gl_Accvouch> vouchs, Boolean isbflag) {
        EntityManager ufdataEntityManager=ufdataEntityManagerfactory.createEntityManager();
        Integer maxyear=uaPeriodRepository.findMaxPeriodByCAcc_id(info.getZhangtaohao());
        Integer maxperiod=glMendRepository.MaxPeriodIsbFlag(info)==null?0:glMendRepository.MaxPeriodIsbFlag(info);
        //得到会计凭证的最大期间和最大凭证号
        Query maxvouchinoid=ufdataEntityManager.createQuery("select u.iperiod as iperiod,max(u.ino_id)+1 as ino_id,max(u.dbill_date) as dbill_date from  Gl_Accvouch  u where  u.iperiod=(" +
                 "select  max(u1.iperiod) from  Gl_Accvouch  u1 where u1.iperiod between  1 and 12) group by u.iperiod");
        Gl_Accvouch maxvouchinfo=new Gl_Accvouch();
        if(!maxvouchinoid.getResultList().isEmpty()) {
            Object[] res = (Object[]) maxvouchinoid.getSingleResult();
            //当期最大凭月份和最大凭证号
            maxvouchinfo.setIperiod(((Integer) res[0])+(isbflag?1:0));
            maxvouchinfo.setIno_id(isbflag?1:(Integer) res[1]);
            maxvouchinfo.setDbill_date(isbflag?new Date():(Date) res[2]);
        }
        else
        {
            maxvouchinfo.setIperiod(1);
            maxvouchinfo.setIno_id(1);
            maxvouchinfo.setDbill_date(new Date());
        }
       ///如果最大会计年大于当前会计年且会计期间为12，更改数据库连接最大会计年度
        if(maxyear>info.getYear())
        {
            DriverManagerDataSource changedatasource=(DriverManagerDataSource)dataSource;
            Pattern pattern=Pattern.compile("\\d{3}_\\d{2}");
            Matcher mather=pattern.matcher(changedatasource.getUrl());
            if(pattern.matcher(changedatasource.getUrl()).find())
            {
                changedatasource.setUrl(mather.replaceAll(info.getZhangtaohao()+"_"+maxyear));
            }
            LocalDateTime billdate=LocalDateTime.of(maxyear,1,7,0,0,0);
            maxvouchinfo.setDbill_date(Date.from(billdate.atZone(ZoneId.systemDefault()).toInstant()));
            maxvouchinfo.setIperiod(1);
            maxvouchinfo.setIno_id(1);
            maxvouchinfo.setDbill_date(new Date());
        }
        else if(maxyear<=info.getYear())
        {
            if(maxvouchinfo.getIperiod()<maxperiod)
            {
                maxvouchinfo.setIperiod(maxperiod+1);
                LocalDateTime dateTime=LocalDateTime.of(info.getYear(),maxperiod+1,1,0,0,0);
                maxvouchinfo.setDbill_date(changeformatedatetodate(dateTime));
                maxvouchinfo.setIno_id(1);
            }


        }
        EntityTransaction transaction=null;
        try {

             BigDecimal mdsum=BigDecimal.valueOf(0);
             BigDecimal mcsum=BigDecimal.valueOf(0);
             Integer inoid=1;
            for (int i=0;i<vouchs.size();i++) {
                ///设置凭证基本信息
                ///凭证日期
                int no=i+1;
                Integer VNO=Integer.valueOf(i+1);
                Gl_Accvouch s=vouchs.get(i);
                //凭证月份
                s.setIperiod(maxvouchinfo.getIperiod());
                s.setCsign("记");
                s.setIsignseq(1);
                s.setIdoc(s.getIdoc()==null?3:s.getIdoc());
                //凭证号
                s.setIno_id(maxvouchinfo.getIno_id());
                s.setDbill_date(maxvouchinfo.getDbill_date());
                s.setInid(Integer.valueOf(no));
                mdsum = mdsum.add(s.getMd());
                mcsum=mcsum.add(s.getMc());
                code needcode=this.codeRepository.findFirstByCcode(s.getCcode(),info);
                if(needcode.getBcus()==true && (s.getCcus_id()==null ||s.getCcus_id().isEmpty()))
                {
                    throw  new RuntimeException("客户编码不能为空");
                }
                if(needcode.getBperson()==true && (s.getCperson_id()==null ||s.getCperson_id().isEmpty()))throw  new RuntimeException("人员编码不能为空");
                if(needcode.getBitem()==true && (s.getCitem_id()==null ||s.getCitem_id().isEmpty()))throw  new RuntimeException("项目不能为空");
                if(needcode.getBdept() && (s.getCdept_id()==null ||s.getCdept_id().isEmpty()))throw  new RuntimeException("部门不能为");

            };

            if(mdsum.floatValue()!=mcsum.floatValue())
            {
                throw  new RuntimeException("凭证借贷金额不平");

            }
            transaction=ufdataEntityManager.getTransaction();
            transaction.begin();
            vouchs.forEach(vs->ufdataEntityManager.persist(vs));
            ufdataEntityManager.clear();
            transaction.commit();

            LocalDateTime insertdata=changeformatedatetolocaldate(maxvouchinfo.getDbill_date());
            return  insertdata.getYear()+"年"+maxvouchinfo.getIperiod()+
                    "月"+maxvouchinfo.getIno_id()+"号";



        }
        catch (Exception e)
        {
            if(transaction!=null)
            {
                transaction.rollback();
            }
              return  e.getMessage();
        }


    }
    private Date  changeformatedatetodate(LocalDateTime date)
    {
        return  Date.from(date.atZone(ZoneId.systemDefault()).toInstant());

    }
    private  LocalDateTime changeformatedatetolocaldate(Date date)
    {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }


    @Override
    public List<Gl_Accvouch> dymamicvouchs(@Qualifier("ufdatainfo") ufdatabasebasic info, vouchcontioan vouch) {
        BigDecimal mb=BigDecimal.valueOf(0);
        Date begindate=ToolUtil.changeformatedatetodate(vouch.getBegindate());
        Date enddate=ToolUtil.changeformatedatetodate(vouch.getEnddate());
        GL_AccSum  GL_AccSum=this.GetYue(info,vouch);
        if(GL_AccSum==null)
        {
            GL_AccSum=new GL_AccSum();
            GL_AccSum.setMe(BigDecimal.valueOf(0));
            GL_AccSum.setMb(BigDecimal.valueOf(0));
            GL_AccSum.setEndd_c("平");
            GL_AccSum.setCbegind_c("平");
        }
       DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd");
        EntityManager ufdataEntityManager=this.ufdataEntityManagerfactory.createEntityManager();
        List<Gl_Accvouch> glvouchs=new LinkedList<>();
        Gl_Accvouch firstvocuhs=new Gl_Accvouch();
        firstvocuhs.setCdigest("期初余额");
        firstvocuhs.setDbill_date(ToolUtil.changeformatedatetodate(vouch.getBegindate()));
        firstvocuhs.setCcend_c(vouch.getBegindate().getMonthValue()==1?GL_AccSum.getCbegind_c():GL_AccSum.getEndd_c());
        firstvocuhs.setMe(vouch.getBegindate().getMonthValue()==1?GL_AccSum.getMb():GL_AccSum.getMe());
        glvouchs.add(firstvocuhs);
   try {


       TypedQuery<Gl_Accvouch> selectquery = ufdataEntityManager.createQuery("" +
                       "select u from Gl_Accvouch  u where dbill_date between  :begindate and :enddate " +
                       " and ( :cusid is null  or u.ccus_id=:cusid)" +
                       " and ( :depid is null  or u.cdept_id=:depid)" +
                       " and (  :personid is null  or u.cperson_id=:personid)" +
                       " and (:inoid is null or u.ino_id=:inoid)" +
                       " and (:ccode is null  or u.ccode like  concat(:ccode,'%') )" +
                       " and (:itemid='' or :itemid is null  or u.citem_id=:itemid)" +
                       "and (:supid is null  or u.csup_id=:supid) order by  u.iperiod,u.ino_id", Gl_Accvouch.class)
               .setParameter("begindate", begindate)
               .setParameter("enddate", enddate)
               .setParameter("cusid", vouch.getCcus_id())
               .setParameter("depid", vouch.getCdept_id())
               .setParameter("personid", vouch.getCperson_id())
               .setParameter("inoid", vouch.getIno_id())
               .setParameter("ccode", vouch.getCcode())
               .setParameter("itemid", vouch.getCitem_id())
               .setParameter("supid", vouch.getCsup_id());


       List<Gl_Accvouch> result = selectquery.getResultList();
       int index = 0;
       Calendar calendar = new GregorianCalendar();
       BigDecimal summd = BigDecimal.ZERO;
       BigDecimal summc = BigDecimal.ZERO;
       for (Gl_Accvouch r : result) {

           calendar.setTime(r.getDbill_date());
           calendar.add(Calendar.DATE, 1);
           r.setDbill_date(calendar.getTime());
           Gl_Accvouch last = glvouchs.get(index);
           BigDecimal addsum = last.getCcend_c().equals("借") ? r.getMd().subtract(r.getMc()) : r.getMc().subtract(r.getMd());
           BigDecimal me = last.getMe().add(addsum);
           r.setMe(me.abs());
           if ((me.compareTo(BigDecimal.ZERO) > 0 && last.getMe().compareTo(BigDecimal.ZERO) > 0) || (me.compareTo(BigDecimal.ZERO) < 0 && last.getMe().compareTo(BigDecimal.ZERO) < 0)) {

               r.setCcend_c(last.getCcend_c());
           } else if (me.compareTo(BigDecimal.ZERO) == 0) {
               r.setCcend_c("平");
           } else {
               r.setCcend_c(last.getCcend_c().equals("借") ? "贷" : "借");
           }
           summd = summd.add(r.getMd());
           summc = summc.add(r.getMc());

           //  r.setCcend_c(me.compareTo(BigDecimal.ZERO)>0?last.getCcend_c():(me.compareTo(BigDecimal.ZERO)==0?"平":(last.getCcend_c().equals("借")?"贷":"借")));
           glvouchs.add(r);
           index++;


       }
       Gl_Accvouch sumvouchs = new Gl_Accvouch();
       sumvouchs.setMd(summd);
       sumvouchs.setMc(summc);
       sumvouchs.setDbill_date(ToolUtil.changeformatedatetodate(vouch.getEnddate()));
       sumvouchs.setCdigest("合计");
       glvouchs.add(sumvouchs);
       return glvouchs;
   }
   catch (Exception e)
   {
       return  null;
   }

        //return null;
    }

    /**
     * 取得年初余额
     * @param info
     * @param ufvc
     * @return
     */
    @Override
    public GL_AccSum GetYue(ufdatabasebasic info, vouchcontioan ufvc)
    {

        try {

            Date begindate=ToolUtil.changeformatedatetodate(ufvc.getBegindate());
            Date enddate=ToolUtil.changeformatedatetodate(ufvc.getEnddate());
            code c=this.codeRepository.findFirstByCcode(ufvc.getCcode(),info);
            GL_AccSum result=new GL_AccSum();
            EntityManager entityManager = this.ufdataEntityManagerfactory.createEntityManager();

            // if(!c.getBitem() && !c.getBsup() && !c.getBdept() && !c.getBperson())
             {
                 TypedQuery<GL_AccSum> selectquery = entityManager.createQuery("select glsum from GL_AccSum glsum where ccode=:ccode and iperiod=1", GL_AccSum.class);
                 selectquery.setParameter("ccode", ufvc.getCcode());
                result = selectquery.getSingleResult();
             }

            if (!ToolUtil.strisNull(ufvc.getCsup_id())) {
             TypedQuery<Gl_accass>   selectass= entityManager.createQuery("select glsum from Gl_accass  glsum where ccode=:ccode and iperiod=1 and glsum.csup_id=:csupid", Gl_accass.class);
                selectass.setParameter("ccode", ufvc.getCcode());
                selectass.setParameter("csupid", ufvc.getCsup_id());
              result=fromglacc(selectass.getSingleResult());

            }
            if (!ToolUtil.strisNull(ufvc.getCitem_id())) {
                TypedQuery<Gl_accass>   selectass= entityManager.createQuery("select glsum from Gl_accass  glsum where ccode=:ccode and iperiod=1  and glsum.citem_id=:citemid", Gl_accass.class);
                selectass.setParameter("ccode", ufvc.getCcode());
                selectass.setParameter("citemid", ufvc.getCitem_id());
               result= fromglacc(selectass.getSingleResult());
            }
            if (!ToolUtil.strisNull(ufvc.getCdept_id())) {
               TypedQuery<Gl_accass> selectass = entityManager.createQuery("select glsum from Gl_accass  glsum where ccode=:ccode and iperiod=1 and glsum.cdept_id=:cdeptid", Gl_accass.class);
                selectass.setParameter("ccode", ufvc.getCcode());
                selectass.setParameter("cdeptid", ufvc.getCdept_id());
                result=  fromglacc(selectass.getSingleResult());
            }
            if (!ToolUtil.strisNull(ufvc.getCperson_id())) {
                TypedQuery<Gl_accass> selectass = entityManager.createQuery("select glsum from Gl_accass  glsum where ccode=:ccode and iperiod=1 and glsum.cperson_id=:cpersonid", Gl_accass.class);
               selectass.setParameter("ccode", ufvc.getCcode());
                selectass.setParameter("cpersonid", ufvc.getCperson_id());
                result= fromglacc(selectass.getSingleResult());
            }
            if (!ToolUtil.strisNull(ufvc.getCcus_id())) {
              TypedQuery<Gl_accass>  selectass = entityManager.createQuery("select glsum from Gl_accass  glsum where ccode=:ccode and iperiod=1 and glsum.ccus_id=:ccusid", Gl_accass.class);
                selectass.setParameter("ccode", ufvc.getCcode());
                selectass.setParameter("ccusid", ufvc.getCcus_id());
                result= fromglacc(selectass.getSingleResult());
            }
           Integer month=begindate.toInstant().atOffset(ZoneOffset.UTC).getMonthValue();
            Query glsumvouchs=entityManager.createQuery("select sum(g.md) as md," +
                    "sum(g.mc)  as mc from  Gl_Accvouch g where g.ccode like  concat(:ccode,'%') and g.iperiod between  1 and :endiperiod " +
                    "and (:cpsn_num is null  or g.cperson_id=:cpsn_num)" +
                            "  and (:cdept_num is null  or g.cdept_id=:cdept_num)" +
                            " and (:ccus_num is null or g.csup_id=:ccus_num   )" +
                            " and (:itemid is null or (g.citem_id=:itemid and g.citem_class=:itemclass))" +
                            " and (:supid is null  or g.csup_id=:supid)")
                    .setParameter("ccode",ufvc.getCcode())
                    .setParameter("endiperiod",begindate.toInstant().atOffset(ZoneOffset.UTC).getMonthValue()==1?1:begindate.toInstant().atOffset(ZoneOffset.UTC).getMonthValue()-1)
                    .setParameter("cpsn_num",ufvc.getCperson_id())
                    .setParameter("cdept_num",ufvc.getCdept_id())
                    .setParameter("ccus_num",ufvc.getCcus_id())
                    .setParameter("itemid",ufvc.getCitem_id()).setParameter("supid",ufvc.getCsup_id())
                    .setParameter("itemclass",c.getCass_item());
              Object[] glvouchs= (Object[])glsumvouchs.getSingleResult();
              result.setMd(BigDecimal.valueOf(glvouchs[0]==null?0:Double.parseDouble(glvouchs[0].toString())));
              result.setMc(BigDecimal.valueOf(glvouchs[1]==null?0:Double.parseDouble(glvouchs[1].toString())));
              BigDecimal summd=result.getCendd_c_engl().equals("Cr")?result.getMc().subtract(result.getMd()):result.getMd().subtract(result.getMc());
              if(result.getMb()==null)result.setMb(BigDecimal.ZERO);
              BigDecimal me= result.getMb().add(summd);
              result.setMe(me.abs());
              if(me.compareTo(BigDecimal.ZERO)==0)
              {
                  result.setEndd_c("平");
                  result.setCendd_c_engl("-");
              }
              else {
                  result.setCendd_c_engl(me.compareTo(BigDecimal.ZERO) > 0 ? result.getCbegind_c_engl() : (result.getCbegind_c_engl()).equals("Dr") ? "Cr" : "Dr");
                  result.setEndd_c(result.getCendd_c_engl().equals("Cr") ? "贷" : "借");
              }

            return result;
        }
        catch (Exception e)
        {
            return  null;
        }


    }
    private  GL_AccSum fromglacc(Gl_accass glAccass)
    {
        GL_AccSum glAccSum=new GL_AccSum();
        glAccSum.setCcode(glAccass.getCcode());
        glAccSum.setIperiod(glAccass.getIperiod());
        glAccSum.setMc(glAccass.getMc());
        glAccSum.setMb(glAccass.getMb());
        glAccSum.setMe(glAccass.getMe());
        glAccSum.setMd(glAccass.getMd());
        glAccSum.setCendd_c_engl(glAccass.getCendd_c_engl());
        glAccSum.setEndd_c(glAccass.getCendd_c());
        glAccSum.setCbegind_c(glAccass.getCbegind_c());
        glAccSum.setCbegind_c_engl(glAccass.getCbegind_c_engl());
        return  glAccSum;
    }

    /**
     * 根据凭证模板确定增值税科目
     * @param info
     * @param needvm
     * @return
     */
    @Override
     public vouchmanager getvmbycode(ufdatabasebasic info,vouchmanager needvm)
     {
        EntityManager ufmanager=this.ufdataEntityManagerfactory.createEntityManager();
       //重写贷方金额和借方金额确定

        if((needvm.getMctaxcode()!=null && !needvm.getMctaxcode().equals("")) || (needvm.getMdtaxcode()!=null && !needvm.getMdtaxcode().equals("")))
        {
            try {
                TypedQuery<Gl_Accvouch> codequery = ufmanager.createQuery("select gl from Gl_Accvouch  gl where gl.ccode=:ccode and gl.iperiod between  1 and 12", Gl_Accvouch.class);
                codequery.setParameter("ccode", !needvm.getMctaxcode().isEmpty() ? needvm.getMccode() : needvm.getMdcode());
                codequery.setMaxResults(1);
                Gl_Accvouch nsvouchs = codequery.getResultList().get(0);
                codequery = ufmanager.createQuery("select  gl from Gl_Accvouch  gl where gl.ccode=:ccode and gl.iperiod=:iperiod and gl.ino_id=:inoid", Gl_Accvouch.class)
                        .setParameter("ccode", needvm.getMdtaxcode()!=null && !needvm.getMdtaxcode().isEmpty() ? needvm.getMdtaxcode() : needvm.getMctaxcode()).setParameter("iperiod", nsvouchs.getIperiod()).setParameter("inoid", nsvouchs.getIno_id());
                codequery.setMaxResults(1);
                Gl_Accvouch taxvouch = codequery.getSingleResult();
                needvm.setTaxvalue(taxvouch.getMc().doubleValue()/nsvouchs.getMc().doubleValue());
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
            }
        }
        return  needvm;

     }

    @Override
    public List<code> coderesult(ufdatabasebasic info, String contioan) {
        int iperiod=this.uaPeriodRepository.findMaxPeriodByCAcc_id(info.getZhangtaohao());
        if(info.getYear()>iperiod)
        {
            info.setYear(iperiod);
        }
        return codeRepository.findAllByCcode(contioan,info);
    }


    @Override
    public Map<String,List<axinfo>> getaxinfofromCode(ufdatabasebasic info,String ncode) {

        int iperiod=this.uaPeriodRepository.findMaxPeriodByCAcc_id(info.getZhangtaohao());
        if(info.getYear()>iperiod)info.setYear(iperiod);
        code needcode=codeRepository.findFirstByCcode(ncode,info);
        Map<String,List<axinfo>> result=new HashMap<>();
        String query="";
        String Key="item";
        if(needcode!=null)
        {
            if(needcode.getBsup())
            {
                query="select v.cVenCode,v.cVenName from vendor v";
                Key="sup";

            }
            if(needcode.getBdept())
            {
                query="select d.cDepCode,d.cDepName from Department  d";
                Key="dep";

            }
            if(needcode.getBperson())
            {
                query="select p.cPersonCode,p.cPersonName from person p";
                Key="person";

            }
            if(needcode.getBitem())
            {
                query="select i.citemcode,i.citemname from  fitemss"+needcode.getCass_item()+" i";
                Key="item";
            }
            if(needcode.getBcus())
            {
                query="select c.cCusCode,c.cCusName from customer c";
                Key="cus";
            }
            EntityManager ufdataEntityManager=this.ufdataEntityManagerfactory.createEntityManager();
           if(!ToolUtil.strisNull(query)) {
               Query axquery = ufdataEntityManager.createNativeQuery(query);

               List<axinfo> axlist = axquery.getResultList();
               List<axinfo> axresult = new LinkedList<>();
               for (Object axinfo : axlist) {
                   axinfo axc = new axinfo();
                   Object[] ax = (Object[]) axinfo;
                   axc.title = ax[1].toString();
                   axc.value = ax[0].toString();
                   axresult.add(axc);


               }

               result.put(Key, axresult);
           }
           else
           {
               result.put("nodata",new LinkedList<>());
           }



        }
        else
        {
            result.put("nodata",new LinkedList<>());

        }
        return  result;
    }
    @Data
    public static class  axinfo
    {
        private String title;
        private String value;
    }
}
