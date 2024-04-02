package com.sqds.ufdataManager.model.ufdata;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class vendor {
    @Id
    private  String cVenCode;
    private String  cVenName;
}
