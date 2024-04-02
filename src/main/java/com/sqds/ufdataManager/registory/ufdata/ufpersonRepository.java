package com.sqds.ufdataManager.registory.ufdata;

import com.sqds.ufdataManager.model.ufdata.ufperson;
import com.sqds.ufdataManager.registory.DataDml.ufdatabasebasic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ufpersonRepository extends JpaRepository<ufperson,String>{

    @Query(value = "from ufperson p where p.cPersonCode =?1")
   ufperson findFirstByCPersonCode(String cpersoncode, ufdatabasebasic info);

    @Query(value = "select  top(1) *  from person p where p.cPersonName=?1",nativeQuery = true)
    ufperson findFirstByCPersonName(String cpersonname,ufdatabasebasic info);
    @Query("select  p from  ufperson p where p.cPersonName like concat(?1, '%')" +
            " or p.cPersonName=?1 or ?1=''")
    List<ufperson> findallByPerson_NameOrAndCPsn_Num(String person_name, ufdatabasebasic info);
    @Modifying
    @Transactional
    @Query("insert into ufperson(cPersonCode,cPersonName,cDepCode) values(?1,?2,?3)")
    void insert(String cpersoncode,String cpersonname,String cdepcode,ufdatabasebasic info);




}
