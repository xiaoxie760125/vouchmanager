package com.sqds.vouchdatamanager.registroy;

import com.sqds.vouchdatamanager.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PersonRepsitory  extends JpaRepository<Person,String> {
    @Query("select p from Person p where p.cPsn_Num = ?1")
    Person findFirstByCPsn_Num(String cpsn_num);


    @Query("select  p from  Person p where p.cPsn_Name  like concat(?1, '%')" +
            " or p.cPsn_Num=?1 or ?1=''")
    List<Person> findallByPerson_NameOrAndCPsn_Num(String person_name,String datasource);
}
