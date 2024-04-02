package com.sqds.vouchdatamanager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Date;

/**
 * 删除人员信息
 */
@Entity
@Table(name = "DeleteVouchs")
@Data
public class DeleteVouchs {
    @Id
    private String  vouchcode;
    private int count;
    private  String manme;
    private  String psn_num;
    private  String jiesuanid;
    private  String ufzhangtao;
    private  String ufvouchcode;
    private  Double shishou;
    private  Double tuiguanfei;
    private  String vouchtype;
    private  String vouchid;
    private  String customername;
    private Date vouchdate;
    private  double revenue;
    private  double tax;
    private  double sum;

}
