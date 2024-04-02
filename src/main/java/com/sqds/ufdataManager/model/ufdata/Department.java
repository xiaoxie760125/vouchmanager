package com.sqds.ufdataManager.model.ufdata;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serializable;

@Entity
@Table(name ="department")
@Data
public class Department implements Serializable {
    @Id
    private String cDepCode;
    private  String cDepName;
    private String cOffergrade;
    private  int iDepGrade;
    private  boolean bDepEnd;
  /*  @OneToMany(mappedBy = "cdepcode")
    private List<person> persons;*/
}
