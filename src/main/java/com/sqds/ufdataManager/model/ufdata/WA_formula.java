package com.sqds.ufdataManager.model.ufdata;

import com.sqds.ufdataManager.model.ufdata.keymodel.formulakey;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Data;

@Entity
@Data
@IdClass(formulakey.class)
public class WA_formula {
    @Id
    private String cGzGradeNum;
    private String iGzitem_id;
    @Column(name ="cGzItemFormula")
    private  String  cGZItemFromula;
    @Column(name ="iFormulaNum")
    @Id
    private  Integer iFromulaNum;
}
