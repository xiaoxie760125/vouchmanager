package com.sqds.vouchdatamanager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * 银行信息基本设置
 */
@Data
@Entity
public class bankinf {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int bankid;
    private  String name;
    private  String bankname;
    private String bankcode;
}
