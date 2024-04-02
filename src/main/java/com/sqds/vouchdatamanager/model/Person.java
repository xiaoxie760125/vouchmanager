package com.sqds.vouchdatamanager.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name ="hr_hi_person")
@Data
public class Person {
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
