package com.sqds.ufdataManager.registory.ufdata;

import com.sqds.ufdataManager.model.ufdata.code;
import com.sqds.ufdataManager.registory.DataDml.ufdatabasebasic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface codeRepository extends JpaRepository<code, String> {
    @Query("select c from code c where (?1='' or ?1=null) or (c.ccode like  concat(?1,'%') or c.ccode_name like concat('%',?1,'%')) ")
    List<code> findAllByCcode(String ccode, ufdatabasebasic info);
    @Query("select c from code c where c.ccode=?1 ")
    code findFirstByCcode(String ccode,ufdatabasebasic info);

}
