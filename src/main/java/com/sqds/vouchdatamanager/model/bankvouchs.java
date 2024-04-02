package com.sqds.vouchdatamanager.model;


import com.sqds.filelutil.excelutil.ExceLTitle;
import com.sqds.vouchdatamanager.model.idclass.banvouchsid;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name ="bankinfo")
@IdClass(banvouchsid.class)
public class bankvouchs {
   

    @ExceLTitle(title ="单号")
    @Id
    private String bankinfoid;
    @ExceLTitle(title ="开户行")
    private  String baninfocode;
    @ExceLTitle(title ="户名")
    private  String bankacccode;
    @ExceLTitle(title ="银行账号")
    private  String bankaccvouchcode;
    @ExceLTitle(title ="借方金额")
    private  float md;
    @ExceLTitle(title ="贷方金额")
    private  float mc;
    @ExceLTitle(title ="对方行名")
    private String vouchaccount;
    @ExceLTitle(title ="对方账号")
    @Column(name ="vouchaname")
    private  String vouchname;
    @ExceLTitle(title ="对方户名")
    private  String vouchcustname;
    @ExceLTitle(title ="余额")
    private  float me;
    @ExceLTitle(title ="申请日期")
    private LocalDateTime vouchdate;
    @ExceLTitle(title ="账套号")
    private  String  ufaccount;
    @ExceLTitle(title ="备注")
    private  String cdigest;
    @ExceLTitle(title ="凭证号")
    private String ufvouchcode;
    private  String fvouchcode;
    @ExceLTitle(title ="交易流水号")
    @Id
    private  String vouchno;


    @Override
    public  boolean  equals(Object obj){
        bankvouchs bs=
                (bankvouchs)obj;
        return  bs.getVouchno().equals(this.vouchno) && bs.getBankinfoid().equals(this.bankinfoid);
    }
   
    


}
