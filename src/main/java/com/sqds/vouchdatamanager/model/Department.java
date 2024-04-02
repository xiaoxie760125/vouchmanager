package com.sqds.vouchdatamanager.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * 部门人员信息
 */
@Data
@Entity
@Table
public class Department implements Serializable {
    @Id
    private  String cDepCode;
    private String cOffergrade;
    private  String cDepName;
    private  String iDepGrade;
    private  boolean bDepEnd;
    @OneToMany(mappedBy ="department")
    private Set<CustomerPerson> persons;



}
