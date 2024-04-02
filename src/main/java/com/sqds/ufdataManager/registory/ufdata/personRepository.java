package com.sqds.ufdataManager.registory.ufdata;

import com.sqds.ufdataManager.model.ufdata.person;
import com.sqds.ufdataManager.registory.DataDml.ufdatabasebasic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface personRepository  extends JpaRepository<person,String>,personCustomerRepository{
  @Query(value = "from person p where p.cPsn_Num =?1")
   person findFirstByCPersonCode(String cpersoncode,ufdatabasebasic info);

   @Query(value ="select top(1) * from hr_hi_person p where p.cPsn_Name=?1",nativeQuery = true)
   person findFirstByCPersonName(String cpersonname,ufdatabasebasic info);
    @Query("select  p from  person p where p.cPsn_Name  like concat(?1, '%')" +
            " or p.cPsn_Num=?1 or ?1=''")
    List<person> findallByPerson_NameOrAndCPsn_Num(String person_name,ufdatabasebasic info);

    @Query("from  person  p where p.cPsnAccount=?1")
    person findFirstByCPsnAccount(String psaccount,ufdatabasebasic info);





}
