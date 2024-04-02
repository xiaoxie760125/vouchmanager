package com.sqds.vouchdatamanager.Help;

import com.sqds.ufdataManager.model.ufdata.Gl_Accvouch;
import com.sqds.ufdataManager.registory.DataDml.ufdatabasebasic;
import com.sqds.ufdataManager.registory.ufsystem.Ua_periodRepository;
import com.sqds.vouchandufdataservice.axinfo;
import com.sqds.vouchandufdataservice.ufvouchsumtype;
import com.sqds.vouchandufdataservice.vouchtoglmodel;
import com.sqds.vouchdatamanager.model.newsvouchs;
import com.sqds.vouchdatamanager.model.vouchs;
import com.sqds.vouchdatamanager.model.vouchtype;
import com.sqds.vouchdatamanager.registroy.newsvouchsRepository;
import com.sqds.vouchdatamanager.registroy.vouchcontion;
import com.sqds.vouchdatamanager.registroy.vouchsCustomerRepository;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Transactional
@Service
@Repository
@Aspect
@EnableAspectJAutoProxy(exposeProxy = true,proxyTargetClass = true)
public class vouchsRepositoryHelp implements vouchsCustomerRepository {


    @PersistenceUnit(unitName ="vouchpersistenceunit")
    EntityManagerFactory entityManagerFactory;
    @Autowired
    Ua_periodRepository periodRepository;
    @Autowired
     CodeUtil codeUtil;
    @Autowired
    newsvouchsRepository newsvouchs;




    @Override
    public Integer addvouchs(List<vouchs> nvouchs, String database) {
        EntityManager entity=this.entityManagerFactory.createEntityManager();
        EntityTransaction transaction=entity.getTransaction();
        transaction.begin();
        for (vouchs vouch:nvouchs)
        {

            entity.merge(vouch);

        }
        transaction.commit();
        //entity.flush();
        entity.clear();
        entity.close();
        return 1;
    }

    @Override
    public List<vouchs> getvouchbycontion(vouchcontion vouchcontion, String database) {
      EntityManager entity=this.entityManagerFactory.createEntityManager();
        LocalDateTime begindate=vouchcontion.getBegindate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime enddate=vouchcontion.getEnddate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        List<String> codetest=new ArrayList<String>();
        String[] Code=vouchcontion.getVouchcode()!=null?(vouchcontion.getVouchcode().contains(",")?vouchcontion.getVouchcode().replaceAll("[']","").split(","):new String[]{vouchcontion.getVouchcode().replaceAll("[']","")}):new String[]{};

       // String[] vouchcode=vouchcontion.getVouchcode()!=null?vouchcontion.getVouchcode().split(","):new String[]{};
        Query getvouchs=entity.createQuery("select v from vouchs  v where " +
                "v.vouchdate between  :begindate and :enddate " +
                 " and (:customer is null or  v.customername like  concat('%',:customer,'%') or v.buyername like concat('%',:customer,'%') )" +
                " and (:vouchtype is  null  or v.vouchtype=:vouchtype) " +
                " and (:isbuyer is null or v.isbuyer=:isbuyer) "+
                        " and v.ufzhangtao=:ufzhangtao")
                .setParameter("begindate",begindate)
                .setParameter("enddate",enddate)
                .setParameter("customer", vouchcontion.getCustomername())
                .setParameter("vouchtype",vouchcontion.getVouch_type())
                .setParameter("ufzhangtao",vouchcontion.getUfzhangtaohao())
                .setParameter("isbuyer",vouchcontion.isIsend());

        List<vouchs> result=getvouchs.getResultList();

        if(Code.length>0) {
            result =result.stream().filter(s -> Arrays.stream(Code).anyMatch(new Predicate<String>() {
                @Override
                public boolean test(String code) {
                    return s.getVouchcode().equals(code);
                }
            })).collect(Collectors.toList());
        }
        final float[] sumvalue = {0,0,0};
        float sumtaxvalue=0;
        result.forEach(s->{
            sumvalue[0] +=s.getValue();
            sumvalue[1]+=s.getTaxvalue();
            sumvalue[2]+=s.getCount();

        });
        vouchs sum=new vouchs();
        sum.setVouchcode("合计");
        sum.setValue(sumvalue[0]);
        sum.setTaxvalue(sumvalue[1]);
        sum.setSum(sumvalue[0]+sumvalue[1]);
        sum.setCount(sumvalue[2]);
        result.add(sum);

        return  result;





    }


    /**
     * 发票生成凭证结构,增加全汇总逻辑 2024年2月7日
     * @param vouchs
     * @param model
     * @param database
     * @return
     */

    @Override
    public List<Gl_Accvouch> fromvouchstogl(List<vouchs> vouchs, vouchtoglmodel model, String database) {
      Float sum=0f;

      Float sumtax=0f;
      Float sumvalue=0f;
        ufdatabasebasic info=new ufdatabasebasic();
        int iperiod=this.periodRepository.findMaxPeriodByCAcc_id(model.getVouchmanager().getUfzhangtao());
        info.setZhangtaohao(model.getVouchmanager().getUfzhangtao());
        info.setYear(iperiod);
        List<Gl_Accvouch> glAccvouches=new LinkedList<>();
        int inid=model.getUfmy()==ufvouchsumtype.md?(model.getVouchmanager().getMdtaxcode()!=null?3:2):1;
        Gl_Accvouch Gls=new Gl_Accvouch();
        for(vouchs v:vouchs)
        {
            Gls.setInid(inid);
            Gls.setCbill(model.getCbill());
            Gls.setIdoc(model.getIdoc());
            //进项发票逻辑
            if(v.isIsbuyer())            {
                Gls.setCdigest("付"+v.getCustomername()+model.getVouchmanager().getVmname());
                      if(model.getUfmy()!= ufvouchsumtype.nosum)
                      {
                          if(model.getUfmy()==ufvouchsumtype.md)
                         {sum+=v.getVouchtype().equals("增值税专用发票")?v.getValue():v.getSum();
                          sumtax+=v.getVouchtype().equals("增值税专用发票")?v.getTaxvalue():0;
                          Gls.setCcode(model.getVouchmanager().getMccode());
                          Gls.setCcodeequal(model.getVouchmanager().getMdcode());
                          Gls.setMd(BigDecimal.valueOf(0));
                          Gls.setMc(new BigDecimal(v.getSum().toString()));

                          codeUtil.setcodeaxinfo(Gls,model.getVouchmanager().getMccode(),model.getMcaxinfo(),info);
                         }
                         else if(model.getUfmy()==ufvouchsumtype.mc)
                          {
                              if(v.getVouchtype().equals("增值税专用发票"))sumtax+=v.getTaxvalue();
                              sum+=v.getSum();
                              Gls.setCcode(model.getVouchmanager().getMdcode());
                              Gls.setCcodeequal(model.getVouchmanager().getMccode());
                              var type=vouchtype.basic;
                              Gls.setMd(new BigDecimal(v.getVouchtype().equals("增值税专用发票")?v.getValue().toString():v.getSum().toString()));
                              Gls.setMc(BigDecimal.valueOf(0));
                              codeUtil.setcodeaxinfo(Gls,model.getVouchmanager().getMdcode(),model.getMdaxinfo(),info);

                          }
                          //借贷方汇总只汇总数据，不生成凭证条目
                          else
                          {
                              sum+=v.getSum();
                              sumvalue+=v.getVouchtype().equals("增值税专用发票")?v.getValue():v.getSum();
                              if(v.getVouchtype().equals("增值税专用发票"))sumtax+=v.getTaxvalue();
                          }

                          if(model.getUfmy()!=ufvouchsumtype.allsum) {
                              glAccvouches.add(Gls);
                          }
                     }
                      else
                      {
                          Gls.setMd(new BigDecimal(v.getVouchtype().equals("增值税专用发票")?v.getValue().toString():v.getSum().toString()));
                          Gls.setMc(BigDecimal.valueOf(0));
                          Gls.setCcode(model.getVouchmanager().getMdcode());
                          Gls.setCcodeequal(model.getVouchmanager().getMccode());
                          Gls.setInid(inid);
                          codeUtil.setcodeaxinfo(Gls,Gls.getCcode(),model.getMdaxinfo(),info);
                          glAccvouches.add(Gls);
                          if(v.getVouchtype().equals("增值税专用发票"))
                          {
                              Gl_Accvouch taxcouch=Gls.clone();
                              taxcouch.setCcode(model.getVouchmanager().getMdtaxcode()==null?codeUtil.getzengezhishui(true,info):model.getVouchmanager().getMdtaxcode());
                              taxcouch.setMd(new BigDecimal(v.getTaxvalue().toString()));
                              taxcouch.setMc(BigDecimal.valueOf(0));
                              glAccvouches.add(taxcouch);
                              inid=taxcouch.getInid()+1;
                          }
                          Gl_Accvouch mcvouch=Gls.clone();
                          if(v.getVouchtype().equals("增值税专用发票"))mcvouch.setInid(inid);
                          mcvouch.setMd(BigDecimal.valueOf(0));
                          mcvouch.setMc(new BigDecimal (v.getSum().toString()));
                          mcvouch.setCcode(model.getVouchmanager().getMccode());
                          codeUtil.setcodeaxinfo(mcvouch,model.getVouchmanager().getMccode(),model.getMcaxinfo(),info);
                          glAccvouches.add(mcvouch);



                      }

            }
            //开出发票
            else
            {

                Gls.setCdigest("收"+v.getBuyername()+model.getVouchmanager().getVmname());
                if(model.getUfmy()!=ufvouchsumtype.nosum)
                {
                    sumtax+=v.getTaxvalue();

                    if(model.getUfmy()==ufvouchsumtype.md)
                    {
                        sum+=v.getSum();
                        Gls.setCcode(model.getVouchmanager().getMccode());
                        Gls.setMc(new BigDecimal(v.getValue().toString()));
                        Gls.setMd(BigDecimal.valueOf(0));
                        codeUtil.setcodeaxinfo(Gls,Gls.getCcode(),model.getMcaxinfo(),info);
                    }
                    else if(model.getUfmy()==ufvouchsumtype.mc)
                    {
                        sum+=v.getValue();
                        Gls.setCcode(model.getVouchmanager().getMdcode());
                     Gls.setCcodeequal(model.getVouchmanager().getMccode());
                     Gls.setMd(BigDecimal.valueOf(v.getSum()));
                     Gls.setMc(BigDecimal.valueOf(0));
                     codeUtil.setcodeaxinfo(Gls,model.getVouchmanager().getMdcode(),model.getMdaxinfo(),info);
                    }
                     else
                    {
                        sum+=v.getSum();
                        sumvalue+=v.getSum();
                        sumtax+=v.getTaxvalue();
                    }
                    if(model.getUfmy()!=ufvouchsumtype.allsum) {
                        glAccvouches.add(Gls);
                    }
                }
                else
                {
                    Gls.setCcode(model.getVouchmanager().getMdcode());
                    Gls.setCcodeequal(model.getVouchmanager().getMccode());
                    Gls.setMd(BigDecimal.valueOf(v.getSum()));
                    Gls.setMc(BigDecimal.valueOf(0));
                    codeUtil.setcodeaxinfo(Gls,model.getVouchmanager().getMdcode(),model.getMdaxinfo(),info);
                    glAccvouches.add(Gls);
                    Gl_Accvouch mcvouch=Gls.clone();
                    mcvouch.setMd(BigDecimal.valueOf(0));
                    mcvouch.setMc(new BigDecimal(v.getValue().toString()));
                    mcvouch.setCcode(model.getVouchmanager().getMccode());
                    codeUtil.setcodeaxinfo(mcvouch,model.getVouchmanager().getMccode(),model.getMcaxinfo(),info);
                    glAccvouches.add(mcvouch);
                    Gl_Accvouch taxvouch=mcvouch.clone();
                    taxvouch.setMd(BigDecimal.valueOf(0));
                    taxvouch.setMc(new BigDecimal(v.getTaxvalue().toString()));
                    taxvouch.setCcode(model.getVouchmanager().getMctaxcode()==null?codeUtil.getzengezhishui(false,info):model.getVouchmanager().getMctaxcode());
                    glAccvouches.add(taxvouch);
                }

            }

         Gls=Gls.clone();
        }
        //增加汇总凭证条条目
        if(model.getUfmy()!=ufvouchsumtype.nosum) {
            //if (vouchs.get(0).isIsbuyer()) {
                if (model.getUfmy() == ufvouchsumtype.md) {
                    Gls.setMd(new BigDecimal(sum.toString()));
                    Gls.setMc(BigDecimal.valueOf(0));
                    Gls.setCcode(model.getVouchmanager().getMdcode());
                    Gls.setCcodeequal(model.getVouchmanager().getMccode());
                    codeUtil.setcodeaxinfo(Gls, Gls.getCcode(), model.getMdaxinfo(), info);
                    glAccvouches.add(0, Gls);



                }
                else
                if (model.getUfmy() == ufvouchsumtype.mc)
                {


                    Gls.setCcode(model.getVouchmanager().getMccode());
                    Gls.setCcodeequal(model.getVouchmanager().getMdcode());
                    Gls.setMc(new BigDecimal(sum.toString()));
                    Gls.setMd(BigDecimal.valueOf(0));
                    codeUtil.setcodeaxinfo(Gls, model.getMcaxinfo(), info);
                    glAccvouches.add(Gls);


                }
                else
                {
                    Gls.setCcode(model.getVouchmanager().getMdcode());
                    Gls.setCcodeequal(model.getVouchmanager().getMccode());
                    Gls.setMd(new BigDecimal(sum.toString()));
                    Gls.setMc(new BigDecimal(0));
                    codeUtil.setcodeaxinfo(Gls, Gls.getCcode(), model.getMdaxinfo(), info);
                    glAccvouches.add(Gls);
                    Gls=Gls.clone();
                    Gls.setCcode(model.getVouchmanager().getMccode());
                    Gls.setCcodeequal(model.getVouchmanager().getMdcode());
                    Gls.setMd(BigDecimal.valueOf(0));
                    Gls.setMc(new BigDecimal(sumvalue.toString()));
                    codeUtil.setcodeaxinfo(Gls,Gls.getCcode(), model.getMcaxinfo(), info);
                    glAccvouches.add(Gls);
                }
            if (sumtax > 0) {
                Gl_Accvouch taxvouch = Gls.clone();
                taxvouch.setCcode(vouchs.get(0).isIsbuyer()?(model.getVouchmanager().getMdtaxcode()==null?codeUtil.getzengezhishui(true,info):model.getVouchmanager().getMdtaxcode()):(model.getVouchmanager().getMctaxcode()==null?codeUtil.getzengezhishui(false,info):model.getVouchmanager().getMctaxcode()));
                taxvouch.setMd(vouchs.get(0).isIsbuyer()?new BigDecimal(sumtax.toString()):BigDecimal.valueOf(0));
                taxvouch.setMc(vouchs.get(0).isIsbuyer()?BigDecimal.valueOf(0):new BigDecimal(sumtax.toString()));
                if(vouchs.get(0).isIsbuyer()){glAccvouches.add(1, taxvouch);}
                else {glAccvouches.add(taxvouch);}
            }

            }
        //}

        return glAccvouches;
    }

    /**
     * 从导入发票中进行借票登记
     * @param p
     * @param vs
     * @param info
     * @return
     * @throws Exception
     */

    @Override
    public  Integer updatepersonp(String p, List<vouchs> vs, axinfo info,String database) throws Exception {
      EntityManager entity=this.entityManagerFactory.createEntityManager();

        try {
            EntityTransaction transaction=
                    entity.getTransaction();
            transaction.begin();
            List<newsvouchs> needmerger=new LinkedList<>();
            for(vouchs v:vs) {
                newsvouchs ns = new newsvouchs();
                ns.setVouchcode(v.getVouchcode());
                ns.setJiesuanid("101");
                ns.setVouchtype(v.getVouchtype());
                ns.setCustomername(v.getBuyername());
                ns.setVouchdate(Date.from(v.getVouchdate().toInstant(ZoneOffset.UTC)));
                if (info.getValue().equals(v.getUfzhangtao())) {
                    ns.setMname(info.getTitle());
                } else {
                    throw new Exception("账套编码不正确");
                }
                ns.setSum(BigDecimal.valueOf(v.getSum()));
                ns.setRevenue(BigDecimal.valueOf(v.getValue()));
                ns.setTax(BigDecimal.valueOf(v.getTaxvalue()));
                ns.setCount(v.getCount().intValue());
                ns.setCpsn_num(p);
                needmerger.add(ns);

            }
            this.newsvouchs.Insertorupdatevouchs(needmerger,database);
            return 1;
        }
        catch ( Exception E)
        {
            return  -1;
        }







    }

}
