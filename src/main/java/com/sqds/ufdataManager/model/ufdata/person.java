package com.sqds.ufdataManager.model.ufdata;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "hr_hi_person")
@Data
public class person {
    @Id
    private  String cPsn_Num;
    private  String cPsn_Name;
    private  String cDept_num;
    @Column(name = "vCardNo")
    private String rCardNo;
    private  boolean bPsnPerson;
    private  String cPsnAccount;
    private String  cPsnFAddr;

}
