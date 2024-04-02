package com.sqds.vouchdatamanager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "toudidan")
public class toudidan {
    @Id
    private String vouchcode;
    private double card;
    private  double vouch;

}
