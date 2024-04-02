package com.sqds.vouchdatamanager.model;

import com.sqds.filelutil.excelutil.ExceLTitle;
import com.sqds.ufdataManager.model.ufdata.person;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


/**
 *发票信息
 */

@Entity
@Table(name = "newsvouchs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class newsvouchs   {


    @Id
    @ExceLTitle(title = "发票号码")
    private String vouchcode = "";
    @Transient
    @ExceLTitle(title = "人员姓名")
    private person ufperson;
    @ExceLTitle(title = "份数")
    private int count = 0;
    @ExceLTitle(title = "借票人")
    private String cpsn_num = "";
    @Column
    @Nullable
    @ExceLTitle(title = "结算方式")
    private String jiesuanid = "";

    private String mname = "";
    @Nullable

    private String ufzhangtao = "";
    @Nullable
    @ExceLTitle(title = "凭证号")
    private String ufvouchcode = "";

    @Nullable
    @ExceLTitle(title = "发票种类")
    private String vouchtype = "";
    @Nullable

    private String vouchid = "";
    @Nullable
    @ExceLTitle(title = "购方名称")
    private String customername = "";
    @ExceLTitle(title = "开票日期")
    private Date vouchdate =new Date();
    @ExceLTitle(title = "金额",column ="税前金额")
    private BigDecimal revenue = BigDecimal.valueOf(0);
    @ExceLTitle(title = "税金")
    private BigDecimal tax = BigDecimal.valueOf(0);
    @ExceLTitle(title = "合计金额",column ="金额")
    private BigDecimal sum = BigDecimal.valueOf(0);
    @Nullable
    @ExceLTitle(title = "实收金额")
    private BigDecimal shishou = BigDecimal.valueOf(0);
    @Nullable
    @Column(name = "tuiguanfei")
    @ExceLTitle(title = "推广费")
    private BigDecimal tuiguangfei = BigDecimal.valueOf(0);
    @Nullable
    @ExceLTitle(title = "开票人")
    private String kaipiaoren = "";
    @Nullable
    @ExceLTitle(title = "结算编号")
    private String bankid = "";
    @Nullable
    @ExceLTitle(title = "推广费凭证")
    private String uftvouchcode = "";
    @Nullable
    private String provouchcode = "";
    @Nullable
    @ExceLTitle(title = "是否垫款")
    private Boolean isadvances = false;

    @Nullable
    @OneToMany(mappedBy = "newsvouchs", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<newsVouchsAllocations> newsVouchsAll = new HashSet<newsVouchsAllocations>();

    @Transient
    @ExceLTitle(title ="到款率",isperenage = true)
    private  BigDecimal percentage;



}
