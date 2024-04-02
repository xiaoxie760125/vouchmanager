package com.sqds.vouchdatamanager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table
@Data
public class shoukuanbiaos implements Serializable {
    @Id
    private  String shourkuanid;
    private  String vouchcode;
    private  String  jiessunid;
    private  Double value;
    private Date shoukuandate;
    private  Double tuiguangfei;
}
