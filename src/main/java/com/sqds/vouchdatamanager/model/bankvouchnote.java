package com.sqds.vouchdatamanager.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

/**
 * 银行对账信息
 */
@Entity
@Table(name = "bankvouchnote")
@Data
public class bankvouchnote {
    @Id
    @Column(name ="bankvouchcode")
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    private int bankvouchcode;
    private String fdep;
    private String cdigest;
    @Column(name ="[values]")
    private Double values;
    private String bankcode;
    private Date billdate;
    private String  ufvouchcode;
    private  String mname;
    private  String vtype;
    private  String taxcode;
    private  String vrtype;
    @Transient
    private  bankinf bankinfo;


}
