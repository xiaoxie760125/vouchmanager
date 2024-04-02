package com.sqds.vouchdatamanager.model;

import com.sqds.filelutil.excelutil.ExceLTitle;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table
@Data
public class vouchs {
    @Id
    @ExceLTitle(title ="发票号码")
    String vouchcode;
    @ExceLTitle(title ="销方识别号")
    String customercode;
    @ExceLTitle(title ="销方名称")
    String customername;
    @ExceLTitle(title ="购方识别号")
    String  buyercode;
    @ExceLTitle(title ="购买方名称")
    String  buyername;
    @ExceLTitle(title ="开票日期")
    LocalDateTime vouchdate;
    @ExceLTitle(title ="税收分类编码")
    String taxfilter;
    @ExceLTitle(title ="特定业务类型")
    String type;
    @ExceLTitle(title ="货物或应税劳务名称")
    String typename;
    @ExceLTitle(title ="单位")
    String unit;
    @ExceLTitle(title ="数量")
    Float count;
    @ExceLTitle(title ="单价")
    Float price;
    @ExceLTitle(title ="金额")
    Float value;
    @ExceLTitle(title ="税率")
    Float taxunit;
    @ExceLTitle(title ="税额")
    Float taxvalue;
    @ExceLTitle(title ="价税合计")
    Float sum;
    @ExceLTitle(title ="发票来源")
   String vouchflont;
    @ExceLTitle(title ="发票票种")
    String vouchtype;
    @ExceLTitle(title ="发票状态")
    String vouchstatues;
    @ExceLTitle(title ="开票人")
    String bill;
    @ExceLTitle(title ="备注")
    String cdigest;
    @ExceLTitle(title ="凭证号")
    String ufvouchcode;
    @ExceLTitle(title ="已使用")
    boolean isused;
    @ExceLTitle(title ="凭证号")
    String ufzhangtao;

    boolean isbuyer;
    @Transient
    @ExceLTitle(title ="数电票号码")
    String shudiancode;








}

