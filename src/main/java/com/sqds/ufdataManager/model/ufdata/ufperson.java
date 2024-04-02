package com.sqds.ufdataManager.model.ufdata;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="Person")
@Data
public class ufperson {
    @Id
    private String cPersonCode;
    private String cPersonName;
    private String cDepCode;
}
