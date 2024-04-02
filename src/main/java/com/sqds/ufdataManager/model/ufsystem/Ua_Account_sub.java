package com.sqds.ufdataManager.model.ufsystem;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Ua_Account_sub {
    @Id
    private  String cAcc_id;
    private  Integer iYear;
    private  String cSub_id;
}
