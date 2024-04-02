package com.sqds.ufdataManager.registory.ufdata;

import com.sqds.ufdataManager.model.ufdata.WA_formula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface Wa_formulaRepository  extends JpaRepository<WA_formula,String >{

    @Query("select w from WA_formula w")
    List<WA_formula>  findallformula();

}
