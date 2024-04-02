package com.sqds.ufdataManager.model.ufdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sqds.filelutil.excelutil.ExceLTitle;
import com.sqds.ufdataManager.model.ufdata.keymodel.wa_gzdatakey;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table
@Data
@IdClass(wa_gzdatakey.class)
public class Wa_GZData {
    @Id
    private String cGZGradeNum;
    @Id
    @ExceLTitle(title= "人员编码")
    @JsonProperty("cPsn_Num")
    private String cPsn_Num;
    @ExceLTitle(title= "人员姓名")
    @JsonProperty("cpsn_Name")
    private String cPsn_Name;
    private  String cDept_Num;
    private String cLkxcode;
    private Integer iPsnGrd_id;
    @Id
    @ExceLTitle(title ="月")
    @JsonProperty("imonth")
    private  Integer iMonth;
    @Id
    @ExceLTitle(title ="年")
    private  Integer iYear;
    @ExceLTitle(title ="会计月份")
    private int iAccMonth;
    private boolean bDCBZ;
    private boolean bTFBZ;
    private String cPreLkxcode;
    private String cPreDeptnum;
    private boolean bLastFlag;
    @Transient()
    private String cdepname;


    private int iRecordID;






}
