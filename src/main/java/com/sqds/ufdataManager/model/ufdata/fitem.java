package com.sqds.ufdataManager.model.ufdata;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class fitem {
    @Id
    private int id;
    private  String citem_class;
    private  String citem_name;
    private  String citem_text;
    private  int crule;
    private  String ctable;
    private String cClasstable;
}
