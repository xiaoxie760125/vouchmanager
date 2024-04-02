package com.sqds.vouchdatamanager.Help;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component("cvouch")
public class vouchcontion {


    private String vouchcode;
    private String cpsn_num;
    private  String customername;
    private Date begindate;
    private  Date enddate;
    private  Integer count;
    private  String vouch_type;
    private  boolean isend;
    private  String mname;
    private  boolean isshoukuan=false;
    private  boolean isyeji=false;
    //取基础数据账套号
    private  String ufpzhangtao;

}
