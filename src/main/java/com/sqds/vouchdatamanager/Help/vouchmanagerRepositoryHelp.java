package com.sqds.vouchdatamanager.Help;
import com.sqds.vouchdatamanager.model.vouchmanager;
import com.sqds.vouchdatamanager.registroy.vmCustomer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
@Repository
@Aspect
@EnableAspectJAutoProxy(exposeProxy = true,proxyTargetClass = true)
public class vouchmanagerRepositoryHelp implements    vmCustomer{
@PersistenceContext(unitName ="vouchpersistenceunit")
    EntityManager querymanager;

    @Override
    public List<vouchmanager> getvouchmanagerByufcode(String ccode, String datasource) {

    Query  query=querymanager.createQuery("select  u from vouchmanager  u where u.ufzhangtao= ?1").setParameter("1",ccode);
    return  query.getResultList();


    }

    @Override
    public vouchmanager addvouchmanager(vouchmanager vouchmanager, String database) {

        Query selectquery=querymanager.createQuery("select  u from vouchmanager  u where u.vmcode= :vmcode").setParameter("vmcode",vouchmanager.getVmcode());
        vouchmanager list=new vouchmanager();
        try {
            list=(vouchmanager) selectquery.getSingleResult();
            list.setMccode(vouchmanager.getMccode());
            list.setMdcode(vouchmanager.getMdcode());
            list.setMctaxcode(vouchmanager.getMdtaxcode());
            list.setMdtaxcode(vouchmanager.getMdtaxcode());
            list.setTaxvalue(vouchmanager.getTaxvalue());
            list.setVmname(vouchmanager.getVmname());
            return  list;

        }catch (NoResultException e){

            Query insertquery = querymanager.createQuery("insert into vouchmanager(vmcode,vmname,mccode,mdcode,taxvalue,mdtaxcode," +
                            "mctaxcode,ufzhangtao,midccode) values(:vmcode,:vmname,:mccode,:mdcode,:taxvalue,:mdtaxcode,:mctaxcode,:ufzhangtaohao,:midcode)")
                    .setParameter("vmcode", vouchmanager.getVmcode())
                    .setParameter("vmname", vouchmanager.getVmname()).setParameter("mccode", vouchmanager.getMccode())
                    .setParameter("mdcode", vouchmanager.getMdcode())
                    .setParameter("taxvalue", vouchmanager.getTaxvalue())
                    .setParameter("mdtaxcode", vouchmanager.getMdtaxcode())
                    .setParameter("mctaxcode", vouchmanager.getMctaxcode())
                    .setParameter("ufzhangtaohao", vouchmanager.getUfzhangtao())
                    .setParameter("midcode", vouchmanager.getMidccode());

            insertquery.executeUpdate();

        }


       this.querymanager.flush();
       this.querymanager.clear();
       this.querymanager.close();
       return  vouchmanager;
    }
}
