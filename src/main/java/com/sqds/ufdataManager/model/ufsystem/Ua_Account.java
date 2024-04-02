package com.sqds.ufdataManager.model.ufsystem;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.stereotype.Repository;

@Entity
@Data
public class Ua_Account {
    @Id
    private   String cAcc_Id;
    private  String cAcc_Name;
    private String cAcc_Path;
    private int iYear;
    private  int iMonth;

}
