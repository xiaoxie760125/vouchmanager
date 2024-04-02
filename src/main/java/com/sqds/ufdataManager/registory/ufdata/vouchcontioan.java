package com.sqds.ufdataManager.registory.ufdata;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class vouchcontioan {
    private  String ccode;
    private  String cdigest;
    private String cdept_id;
    private  String cperson_id;
    private  String ccus_id;
    private String csup_id;
    private  String citem_id;
    private  Integer ino_id;
    private BigDecimal minvalue;
    private  BigDecimal maxvalue;
    private LocalDateTime begindate;
    private  LocalDateTime enddate;

}
