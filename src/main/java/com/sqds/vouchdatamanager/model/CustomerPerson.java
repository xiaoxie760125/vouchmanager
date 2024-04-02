package com.sqds.vouchdatamanager.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 基本人员信息
 */
@Entity
@Table(name ="Person")
@Data
public class CustomerPerson {
    @Id
    private String cPersonCode;
    private String cPersonName;
    private String cDepCode;
    private  String cPersonProp;
    private  String cPersonEmail;
    private  String cPersonPhone;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cDepCode", referencedColumnName = "cDepCode", insertable = false, updatable = false)
    private Department department;
}
