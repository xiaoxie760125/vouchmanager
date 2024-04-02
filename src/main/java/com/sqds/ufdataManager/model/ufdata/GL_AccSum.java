package com.sqds.ufdataManager.model.ufdata;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class GL_AccSum {
    @Id
    private int i_id;
    @Transient
    private String ccodename;
    private String ccode;
    private int iperiod;
    private String cbegind_c;
    private String cbegind_c_engl;
    private String cendd_c_engl;
    @Column(name ="cendd_c")
    private String endd_c;
    private BigDecimal mb;
    private BigDecimal mc;
    private BigDecimal md;
    private BigDecimal me;
    private BigDecimal mb_f;
    private BigDecimal md_f;
    private BigDecimal mc_f;
    public BigDecimal me_f;
    public double nb_s;
    public double nd_s;
    public double nc_s;
    public double ne_s;




}