package com.sqds.vouchdatamanager.Help;

import com.sqds.ufdataManager.model.ufdata.person;
import com.sqds.ufdataManager.registory.DataDml.personhelp;
import com.sqds.ufdataManager.registory.DataDml.ufdatabasebasic;
import com.sqds.ufdataManager.registory.ufsystem.Ua_periodRepository;
import com.sqds.vouchdatamanager.model.bankinf;
import com.sqds.vouchdatamanager.model.bankvouchnote;
import com.sqds.vouchdatamanager.registroy.bankinfRepository;
import com.sqds.vouchdatamanager.registroy.banknodeCustomerRository;
import com.sqds.vouchdatamanager.registroy.vouchcontion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
@Transactional
@Service
@Repository
@Aspect
@EnableAspectJAutoProxy(exposeProxy = true,proxyTargetClass = true)
public class bankvouchnoteRepositoryHelp implements banknodeCustomerRository {
    @PersistenceContext(unitName ="vouchpersistenceunit")
    EntityManager entityManager;
    @Autowired
    personhelp perasoninfo;
    @Autowired
    bankinfRepository bankinfo;
    @Autowired
     Ua_periodRepository uaPeriodRepository;
    @Override
    public int insertorupdate(bankvouchnote bankinfo, String database) {
        try{
            bankvouchnote bankvouchnote = this.entityManager.find(bankinfo.getClass(), bankinfo.getBankvouchcode());
            if(bankvouchnote!=null)
            {
                bankvouchnote.setValues(bankinfo.getValues());
                bankvouchnote.setBilldate(bankinfo.getBilldate());
                bankvouchnote.setBankvouchcode(bankinfo.getBankvouchcode());
                bankvouchnote.setFdep(bankinfo.getFdep());
            }
            else
            {
                this.entityManager.persist(bankinfo);
                List bankinf=this.entityManager.createQuery("select b from bankinf  b where b.bankcode=:bankcode").setParameter("bankcode",bankinfo.getBankinfo().getBankcode()).getResultList();
                if(bankinf.isEmpty()) {
                    this.entityManager.merge(bankinfo.getBankinfo());
                }
            }
            this.entityManager.flush();
            this.entityManager.clear();
            return  1;

        }
        catch (Exception E)
        {
            return  0;
        }

    }

    @Override
    public List getbankvouchnode(vouchcontion voucontion, String zhangtaohao, String database) {
        try {
            List<person> personList=this.perasoninfo.GetPersonFromPsn_num(zhangtaohao);
            Query getbanknode= entityManager.createQuery("select b.billdate,b.cdigest,b.values,b.bankcode,b.ufvouchcode,b.fdep,b.vtype,b.vrtype,b.bankvouchcode from bankvouchnote  b where " +
                    " b.billdate between  :begindate and :enddate  " +
                    "  and (b.mname=:mname or :mname is null)" +
                    " and (b.cdigest like concat('%',:customer,'%') or :customer is null ) order by billdate desc" );
            DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");

            getbanknode.setParameter("begindate",voucontion.getBegindate())
                    .setParameter("enddate",voucontion.getEnddate())
                    .setParameter("customer",voucontion.getCustomername())
                    .setParameter("mname",voucontion.getVouch_type());
            List bankvouchnotes=getbanknode.getResultList();
            List<bankvouchnote> bankvouchnoteslist=new ArrayList<>();
            bankvouchnotes.forEach(v->{
                Object[] vt=(Object[])v;
                bankvouchnote bv=new bankvouchnote();
                bv.setValues(vt[2]==null?0:(Double)vt[2]);
                bv.setBilldate(vt[0]==null?new Date():(Date)vt[0]);
                bv.setCdigest(vt[1]==null?"":vt[1].toString());
                bv.setUfvouchcode(vt[4]==null?"":vt[4].toString());
                bv.setFdep(vt[5]==null?"":vt[5].toString());
                bv.setVtype(vt[6]==null?"":vt[6].toString());
                bv.setVrtype(vt[7]==null?"":vt[7].toString());
                bv.setBankcode(vt[3]==null?"":vt[3].toString());
                bv.setBankvouchcode(vt[8]==null?0:(Integer)vt[8]);
                if(vt[3]!=null)
                {
                    Query firstbaninfo=entityManager.createQuery("select v.bankname,v.bankcode,v.name from  bankinf  v where v.bankcode=?1").setParameter(1,vt[3].toString());

                    bankinf bankinf=new bankinf();
                    Optional<Object> bankinfraw=firstbaninfo.getResultList().stream().findFirst();
                    if(bankinfraw.isEmpty())
                    {



                        Optional<person> person=personList.stream().filter(s->s.getCPsnAccount()!=null && s.getCPsnAccount().equals(vt[3].toString())).findFirst();
                        if(person.isPresent())
                        {
                            bankinf=new bankinf();
                            bankinf.setBankcode(person.get().getCPsnAccount());
                            bankinf.setName(person.get().getCPsn_Name());
                            bankinf.setBankname(person.get().getCPsnFAddr());
                           this.entityManager.createNativeQuery("insert  into bankinf(bankcode,bankname,name) values (?1,?2,?3)")
                                    .setParameter(1,bankinf.getBankcode()).setParameter(2,bankinf.getBankname()).setParameter(3,bankinf.getName()).executeUpdate();


                        }

                    }
                    else
                    {
                        Object[] property=(Object[])bankinfraw.get();
                        bankinf.setBankname(property[0]==null?"":property[0].toString());
                        bankinf.setName(property[2]==null?"":property[2].toString());
                        bankinf.setBankcode(property[1]==null?"":property[1].toString());
                    }
                    if(bankinf.getName()!=null)bv.setBankinfo(bankinf);


                }
                if(bv.getValues()!=null)bankvouchnoteslist.add(bv);
            });
           this.entityManager.flush();
           this.entityManager.clear();
            return bankvouchnoteslist;
        }
        catch (Exception e)
        {
            return  null;
        }

    }

    @Override
    public person getperson(String banckcode,String ufzhangtao, String database) {
        ufdatabasebasic info=new ufdatabasebasic();
        info.setZhangtaohao(ufzhangtao);
        int year= uaPeriodRepository.findMaxPeriodByCAcc_id(ufzhangtao);
         info.setYear(year);
         return  perasoninfo.getpersonbyaccoubt(banckcode,info);


    }
     bankinf getbaninffromperson(String name,String zhangtaohao)
     {
         List<person> personList=this.perasoninfo.GetPersonFromPsn_num(zhangtaohao);
         Optional<person> person=personList.stream().filter(s->s.getCPsn_Name()!=null && s.getCPsn_Name().equals(name)).findFirst();
         bankinf bankinf=new bankinf();
         if(person.isPresent())
         {
             bankinf=new bankinf();
             bankinf.setBankcode(person.get().getCPsnAccount());
             bankinf.setName(person.get().getCPsn_Name());
             bankinf.setBankname(person.get().getCPsnFAddr());
             this.entityManager.createNativeQuery("insert  into bankinf(bankcode,bankname,name) values (?1,?2,?3)")
                     .setParameter(1,bankinf.getBankcode()).setParameter(2,bankinf.getBankname()).setParameter(3,bankinf.getName()).executeUpdate();


         }
         return bankinf;
     }
    @Override
    public bankinf getbankinfo(String banknode, String ufzhangtaohao, String databse) {
        try {
            TypedQuery<bankinf> getbaninfo = this.entityManager.createQuery("select bk from bankinf  bk where :banknode like concat('%',bk.name,'%') ", bankinf.class).setParameter("banknode", banknode);
            return getbaninfo.getResultList().isEmpty() ? getbaninffromperson(banknode, ufzhangtaohao) : (getbaninfo.getResultList().isEmpty() ? null : (bankinf) getbaninfo.getResultList().get(0));
        }
        catch (Exception e)
        {
            return  new bankinf();
        }

    }
}
