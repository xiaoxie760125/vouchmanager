package com.sqds.vouchdatamanager.Help;

import com.sqds.comutil.ToolUtil;
import com.sqds.springjwtstudy.controller.responsemodel.vouchgl;
import com.sqds.ufdataManager.model.ufdata.Gl_Accvouch;
import com.sqds.ufdataManager.model.ufdata.code;
import com.sqds.ufdataManager.registory.DataDml.ufdatabasebasic;
import com.sqds.ufdataManager.registory.ufdata.Gl_AccVouchRepository;
import com.sqds.ufdataManager.registory.ufdata.codeRepository;
import com.sqds.ufdataManager.registory.ufdata.personRepository;
import com.sqds.ufdataManager.registory.ufsystem.Ua_periodRepository;
import com.sqds.vouchandufdataservice.axinfo;
import com.sqds.vouchandufdataservice.ufvouchsumtype;
import com.sqds.vouchandufdataservice.vouchtoglmodel;
import com.sqds.vouchdatamanager.model.bankvouchs;
import com.sqds.vouchdatamanager.registroy.bankvouchsCustomerRepository;
import com.sqds.vouchdatamanager.registroy.vouchcontion;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Transactional
@Service
@Repository
@Aspect
@EnableAspectJAutoProxy(exposeProxy = true,proxyTargetClass = true)
public class bankvouchsRepositoryHelp implements bankvouchsCustomerRepository {

    @Autowired
    @PersistenceUnit(unitName = "vouchpersistenceunit")
    EntityManagerFactory entityManagerFactory;
    @Autowired
    codeRepository codeRepository;
    @Autowired
    Ua_periodRepository periodRepository;
    @Autowired
    personRepository personRepository;
    @Autowired
    Gl_AccVouchRepository glvouchmanager;
    @Autowired
    Ua_periodRepository periodinfo;
    //发票登记增加
    @Autowired
    newsvouchsRepositoryHelp newsvouchsreg;
    @Override
    @Transactional
    public Integer insertorupdatebankinfo(List<bankvouchs> banks, String database) {
        EntityManager entityManager=this.entityManagerFactory.createEntityManager();
       EntityTransaction transaction=entityManager.getTransaction();
       transaction.begin();
        try
        {
        banks.forEach(s->{
              // this.entityManager.remove(s);
               entityManager.merge(s);

        });
        transaction.commit();
        entityManager.clear();
        entityManager.close();
        return 1;
    }catch(Exception e)
    {
        return 0;

    }
        
        
    }

    @Override
    public List<bankvouchs> getbankvouchs(vouchcontion vc, String database) {
        EntityManager entityManager=this.entityManagerFactory.createEntityManager();
        try
        {
        Query getvouchquery=entityManager.createQuery("select  b from bankvouchs  b where b.vouchdate between :begindate and :enddate " +
                " and (:customername is NULL  or b.vouchcustname like concat('%',:customername,'%')) " +
                " and  b.ufaccount=:ufaccount  " +
                "  and ((:isend=0 and b.md=0) or (:isend=1 and b.mc=0))  "+
                " and (:vcode is NULL or b.vouchno like concat('%',:vcode,'%'))"+
                 " and  (:vouch_type is NUll or b.bankacccode=:vouch_type) order by  b.vouchdate ")
                .setParameter("begindate", ToolUtil.changeformatedatetolocaldate(vc.getBegindate()))
                .setParameter("enddate",ToolUtil.changeformatedatetolocaldate(vc.getEnddate())).setParameter("customername",vc.getCustomername())
                .setParameter("vcode",vc.getVouchcode()!=null?vc.getVouchcode().replace("'", ""):null)
                .setParameter("vouch_type",vc.getVouch_type()).setParameter("ufaccount",vc.getUfzhangtaohao())
                .setParameter("isend", vc.isIsend()?1:0);
            List<bankvouchs> vouchs=getvouchquery.getResultList();
            bankvouchs sumbanvouchs=new bankvouchs();
            sumbanvouchs.setVouchdate(vouchs.get(vouchs.size()-1).getVouchdate());
            sumbanvouchs.setVouchno("合计");
            sumbanvouchs.setMe(vouchs.get(vouchs.size()-1).getMe());
            vouchs.forEach(s->{
                sumbanvouchs.setMd(sumbanvouchs.getMd()+s.getMd());
                sumbanvouchs.setMc(sumbanvouchs.getMc()+s.getMc());
            });
            vouchs.add(sumbanvouchs);
            return vouchs;
                    }
        catch (Exception e)        {
            System.out.println(e.getMessage());
            return null;
        }
      
        
    }




    /**
     * 银行转银行凭证
     * @param infos
     * @param model
     * @param database
     * @return
     */
    @Override
    public List<Gl_Accvouch> getbankinfovouchs(List<bankvouchs> infos, vouchtoglmodel model, String database) {
         //结算未赋值税率
         if((model.getVouchmanager().getMdtaxcode()!=null || model.getVouchmanager().getMctaxcode()!=null
          && model.getVouchmanager().getTaxvalue()==0))
         {
           model.setVouchmanager(this.glvouchmanager.getvmbycode(new ufdatabasebasic(model.getVouchmanager().getUfzhangtao()),model.getVouchmanager()));
         }
         int period=this.periodRepository.findMaxPeriodByCAcc_id(model.getVouchmanager().getUfzhangtao());
         ufdatabasebasic ufinfo=new ufdatabasebasic();
         ufinfo.setYear(period);
         ufinfo.setZhangtaohao(model.getVouchmanager().getUfzhangtao());
         List<Gl_Accvouch> vouchsresult=new LinkedList<>();
         BigDecimal sum=BigDecimal.valueOf(0);
         BigDecimal taxsum=BigDecimal.valueOf(0);
         int inid=model.getUfmy()==ufvouchsumtype.md?(model.getVouchmanager().getMdtaxcode()!=null?3:2):1;
         for(bankvouchs binfo:infos)
         {
             Gl_Accvouch Gls=new Gl_Accvouch();
             String cdigest=binfo.getMd()>0?"收":"付";
             Pattern pattern=
                     Pattern.compile("[\\u4e00-\\u9fa5]*");
             Matcher matcher=pattern.matcher(binfo.getCdigest());
             //根据摘要情况生成凭证摘要
             cdigest+=(binfo.getVouchcustname()+(binfo.getCdigest()!=null && matcher.matches()?binfo.getCdigest():model.getVouchmanager().getVmname()));
             Gls.setCbill(model.getCbill());
             //设置凭证张数 2024-3-4
             Gls.setIdoc(model.getIdoc());
             Gls.setCdigest(cdigest);
             BigDecimal value=(binfo.getMd()>0?BigDecimal.valueOf(binfo.getMd()):BigDecimal.valueOf(binfo.getMc())).divide(BigDecimal.valueOf(1+model.getVouchmanager().getTaxvalue()),2,RoundingMode.HALF_UP);
             BigDecimal taxvalue=value.multiply(BigDecimal.valueOf(model.getVouchmanager().getTaxvalue())).setScale(2,RoundingMode.HALF_UP);
             if(model.getUfmy()!= ufvouchsumtype.nosum)
             {
                 sum=sum.add(value);
                 taxsum=taxsum.add(taxvalue);
                 String mdcode=model.getUfmy()==ufvouchsumtype.md?model.getVouchmanager().getMccode():model.getVouchmanager().getMdcode();
                 String codeequal=model.getUfmy()==ufvouchsumtype.md?model.getVouchmanager().getMdcode():model.getVouchmanager().getMccode();
                 Gls.setCcode(mdcode);
                 Gls.setCcodeequal(codeequal);
                 if(model.getUfmy()==ufvouchsumtype.md)
                 {
                     if (model.getVouchmanager().getTaxvalue()>0 && model.getVouchmanager().getMdtaxcode() != null) {

                             Gls.setMc(BigDecimal.valueOf(binfo.getMd() > 0 ? binfo.getMd() : binfo.getMc()));

                     }
                     else
                     {
                         Gls.setMc(value);
                     }
                     Gls.setMd(BigDecimal.valueOf(0));

                 }
                 else
                 {
                     if (model.getVouchmanager().getTaxvalue()>0 && model.getVouchmanager().getMctaxcode() != null) {

                         Gls.setMd(BigDecimal.valueOf(binfo.getMd() > 0 ? binfo.getMd() : binfo.getMc()));

                     }
                     else
                     {
                         Gls.setMd(value);
                     }
                     Gls.setMc(BigDecimal.valueOf(0));
                 }
                // Gls.setMc(model.getUfmy()==ufvouchsumtype.md?(model.getVouchmanager().getMctaxcode()!=null?BigDecimal.valueOf(binfo.getMd()>0?binfo.getMd():binfo.getMc()):value):BigDecimal.valueOf(0));
                 //Gls.setMd(model.getUfmy()==ufvouchsumtype.mc?(model.getVouchmanager().getMdtaxcode()!=null?BigDecimal.valueOf(binfo.getMd()>0?binfo.getMd():binfo.getMc()):value):BigDecimal.valueOf(0));
                 Gls.setCdigest(cdigest);
                 Gls.setInid(inid);
                 setcodeaxinfo(Gls,mdcode,model.getMdaxinfo(),ufinfo);
                 vouchsresult.add(Gls);
                 inid+=1;
             }
             else
             {
                 Gls.setCcode(model.getVouchmanager().getMdcode());
                 Gls.setMd(!(model.getVouchmanager().getMdtaxcode()==null)?value:value.add(taxvalue));
                 Gls.setMc(BigDecimal.valueOf(0));
                 Gls.setCcodeequal(model.getVouchmanager().getMccode());
                 setcodeaxinfo(Gls,Gls.getCcode(),model.getAxinfo(),ufinfo);
                 Gls.setInid(inid);
                 vouchsresult.add(Gls);
                 if(taxvalue.floatValue()>0)
                 {
                     Gl_Accvouch taxgl=Gls.clone();
                     taxgl.setCcode(model.getVouchmanager().getMdtaxcode()!=null?model.getVouchmanager().getMdtaxcode():model.getVouchmanager().getMctaxcode());
                     taxgl.setMd(model.getVouchmanager().getMdtaxcode()!=null?taxvalue:BigDecimal.valueOf(0));
                     taxgl.setMc(model.getVouchmanager().getMdtaxcode()!=null?BigDecimal.valueOf(0):taxvalue);
                      vouchsresult.add(taxgl);

                 }

                     Gl_Accvouch mcvouch= Gls.clone();
                     if(taxvalue.floatValue()>0)
                     {
                         mcvouch.setInid(mcvouch.getInid()+1);
                     }
                     mcvouch.setMc(model.getVouchmanager().getMdtaxcode()==null?value:value.add(taxvalue));
                     mcvouch.setMd(BigDecimal.valueOf(0));
                     mcvouch.setCcode(model.getVouchmanager().getMccode());
                     setcodeaxinfo(mcvouch,mcvouch.getCcode(),model.getMcaxinfo(),ufinfo);
                     mcvouch.setCcodeequal(model.getVouchmanager().getMdtaxcode());
                     vouchsresult.add(mcvouch);
                     inid=mcvouch.getInid()+1;


             }





         }

         if(model.getUfmy()!=ufvouchsumtype.nosum)
         {
            // BigDecimal taxvalue=sum.divide(BigDecimal.valueOf(1+model.getVouchmanager().getTaxvalue())).multiply(BigDecimal.valueOf(model.getVouchmanager().getTaxvalue()));
             Gl_Accvouch sumgl=vouchsresult.get(0).clone();
              sumgl.setInid(model.getUfmy()==ufvouchsumtype.md?1:inid);
              sumgl.setMd(model.getUfmy()==ufvouchsumtype.md?(model.getVouchmanager().getMctaxcode()!=null?sum.add(taxsum):sum):BigDecimal.valueOf(0));
              sumgl.setMc(model.getUfmy()==ufvouchsumtype.md?BigDecimal.valueOf(0):sum);
              sumgl.setCcode(model.getUfmy()==ufvouchsumtype.md?model.getVouchmanager().getMdcode():model.getVouchmanager().getMccode());
              setcodeaxinfo(sumgl,model.getVouchmanager().getMdcode(),model.getAxinfo(),ufinfo);

              vouchsresult.add(model.getUfmy()==ufvouchsumtype.md?0:vouchsresult.size(),sumgl);
              if(model.getVouchmanager().getTaxvalue()>0)
              {
                  Gl_Accvouch taxmcsum=sumgl.clone();
                  taxmcsum.setCcode(model.getVouchmanager().getMctaxcode()==null?model.getVouchmanager().getMdtaxcode():model.getVouchmanager().getMctaxcode());
                  taxmcsum.setMd(model.getVouchmanager().getMdtaxcode()==null?BigDecimal.valueOf(0):taxsum);
                  taxmcsum.setMc(model.getVouchmanager().getMdtaxcode()==null?taxsum:BigDecimal.valueOf(0));
                  taxmcsum.setInid(model.getUfmy()==ufvouchsumtype.md?2:inid++);
                  vouchsresult.add(model.getUfmy()==ufvouchsumtype.md?1:vouchsresult.size(),taxmcsum);
              }

         }
        return vouchsresult;
    }

   private axinfo setcodeaxinfo(Gl_Accvouch gl, String code, Map<String,axinfo> info, ufdatabasebasic uainfo)
   {
       code ncode=this.codeRepository.findFirstByCcode(code,uainfo);

       if(ncode.getBperson())
       {
           var person=this.personRepository.findFirstByCPersonCode(info.get("person").getValue(),uainfo);
           gl.setCperson_id(person.getCPsn_Num());
           gl.setCdept_id(person.getCDept_num());
           return  info.get("person");

       }
       if(ncode.getBsup())
       {
           gl.setCsup_id(info.get("sup").getValue());
           return  info.get("sup");
       }
       if(ncode.getBcus())
       {  gl.setCcus_id(info.get("cus").getValue());
           return  info.get("cus");
       }
       if(ncode.getBitem())
       {
           gl.setCitem_id(info.get("item").getValue());
           gl.setCitem_class(ncode.getCass_item());
           return  info.get("item");
       }
       if(ncode.getBdept())
       {
           gl.setCdept_id(info.get("dep").getValue());
           return  info.get("dep");
       }
       return  null;
   }



  
   public String insertglvouchsfromvouchs(vouchgl<bankvouchs,vouchtoglmodel> vouchs,String databse)
   {
    String ufvouchcode="";
      List<Gl_Accvouch> gls=this.getbankinfovouchs(vouchs.getVouchs(), vouchs.getModels(), databse);
      ufdatabasebasic info=new ufdatabasebasic();
      info.setZhangtaohao(vouchs.getModels().getVouchmanager().getUfzhangtao());
      info.setYear(periodinfo.findMaxPeriodByCAcc_id(vouchs.getModels().getVouchmanager().getUfzhangtao()));
       ufvouchcode=this.glvouchmanager.Insertvouch(info, gls, vouchs.getModels().getIsfirstvouch());
       
      String pattern="\\d{4}年\\d{1,2}月\\d{1,4}号";
      Pattern p=Pattern.compile(pattern);
      if(p.matcher(ufvouchcode).find())
      {
        for(var v:vouchs.getVouchs())
          {
                v.setUfvouchcode(ufvouchcode);
            }
          insertorupdatebankinfo(vouchs.getVouchs(), databse);
      }
    
      return ufvouchcode;


  
   }



}
