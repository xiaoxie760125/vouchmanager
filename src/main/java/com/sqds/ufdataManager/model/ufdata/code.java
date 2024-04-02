package com.sqds.ufdataManager.model.ufdata;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "code")
public class code {
    @Id
    private String ccode;
    private  String cclass;
    private  String cclass_engl;
    private String cclassany;
    private  String cclassany_engl;
    private  String ccode_name;
    private  String ccode_engl;
    private  int igrade;
    private  Boolean bend;
    private Boolean bperson;
    private Boolean bcus;
    private Boolean bdept;
    private  Boolean bsup;
    private  Boolean bitem;
    private String cass_item;



}
