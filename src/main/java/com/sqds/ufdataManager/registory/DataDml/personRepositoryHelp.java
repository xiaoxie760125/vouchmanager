package com.sqds.ufdataManager.registory.DataDml;

import com.sqds.ufdataManager.model.ufdata.person;
import com.sqds.ufdataManager.registory.ufdata.personCustomerRepository;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
public class personRepositoryHelp  implements  personCustomerRepository{


    @PersistenceUnit(unitName ="ufdata")
    private EntityManagerFactory entityManager;
    ///动态生成工资员工
   @Transactional
    @Override
    public person dynamicgetgzperson(String P, ufdatabasebasic info) {
       EntityManager entityManager1=entityManager.createEntityManager();
       EntityTransaction entityTransaction=entityManager1.getTransaction();
        try {

            entityTransaction.begin();
            person pn=new person();
            Query exitquery= entityManager1.createQuery("select p from person p  join Department  dp on dp.cDepCode=p.cDept_num where p.cPsn_Name=:name ").setParameter("name",P);
            List<person> exitperson=exitquery.getResultList();
            if(!exitperson.isEmpty())
            {
                return  exitperson.get(0);
            }

            Query sigularquery= entityManager1.createQuery("select p.cPsn_Name,p.cPsn_Num,p.cDept_Num from Wa_GZData  p where p.cPsn_Name=:name ");
            sigularquery.setParameter("name",P);
            List persn=sigularquery.getResultList();
            if(persn.size()>0)
            {
                exitquery= entityManager1.createQuery("select p from person p where p.cPsn_Name=:num ").setParameter("num",P);
                person p=(person) exitquery.getSingleResult();
                Object[]ps=(Object[]) persn.get(0);
                //如果人员存在的化,更改人员中正确的部门
                if(p!=null){
                    p.setCDept_num((String)ps[2]);
                    entityManager1.merge(p);
                    entityTransaction.commit();
                    return  p;}
                pn.setCPsn_Num((String)ps[1]);
                pn.setCDept_num((String)ps[2]);
                pn.setCPsn_Name(P);

            }
            else
            {
                sigularquery=entityManager1.createQuery(" from person p ");
                List<person> personList=sigularquery.getResultList();
                Optional<person> dymaperson=personList.stream().max(Comparator.comparingInt(s->Integer.parseInt(s.getCPsn_Num())));
                if(dymaperson.isPresent())
                {
                    pn.setCPsn_Name(P);
                    pn.setCPsn_Num(String.valueOf(Integer.parseInt(dymaperson.get().getCPsn_Num())+1));
                    pn.setCDept_num(dymaperson.get().getCDept_num());

                }
            }

            entityManager1.persist(pn);
            entityTransaction.commit();


            return pn;


        }
        catch (Exception E)
        {
            entityTransaction.rollback();
            return  new person();
        }
        finally {
            entityManager1.close();

        }





    }
}
