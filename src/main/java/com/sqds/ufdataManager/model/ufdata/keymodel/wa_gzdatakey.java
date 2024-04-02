package com.sqds.ufdataManager.model.ufdata.keymodel;

import lombok.Data;

import java.io.Serializable;

@Data
public class wa_gzdatakey implements Serializable {
    private  String cGZGradeNum;
    private  String cPsn_Num;
    private String cPsn_Name;
    private Integer iYear;
    private   Integer iMonth;
}
