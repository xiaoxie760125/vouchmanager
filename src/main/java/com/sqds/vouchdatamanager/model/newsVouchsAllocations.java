package com.sqds.vouchdatamanager.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 发票分配信息
 */
@Entity
@Table
@Data
public class newsVouchsAllocations {
    @Id
    private String allcode;

   // private  String awdcode;
    private  String cpsn_num;
    private int count;
    private  String ufvouchcode;
    private BigDecimal shishou;
    private  BigDecimal tuiguanfei;
    private  boolean isorpsn;
    private  boolean isend;
    private boolean istoudi;
    private Date enddate;
    private  Date toudidate;
    @ManyToOne(targetEntity=newsvouchs.class)
    @JoinColumn(name = "awdcode",referencedColumnName = "vouchcode")
    private  newsvouchs newsvouchs;
}
