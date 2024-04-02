package com.sqds.vouchdatamanager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 报纸推广费报款基本设置
 */
@Entity
@Table(name ="tuiguangfei")
@Data
public class tuiguangfei {
    @Id
    private  String awardtypecode;
    private  String awartypename;
    private  float value;
    private float price;

}
