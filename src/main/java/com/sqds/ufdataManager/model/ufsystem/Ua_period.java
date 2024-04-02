package com.sqds.ufdataManager.model.ufsystem;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table
public class Ua_period {
    @Id
    private String cAcc_id;
    private  int iYear;
    private  int ild;
    private LocalDateTime dBegin;
    private LocalDateTime dEnd;
    private boolean blsDelete;
}
