package com.sqds.anthdatamanange.data;


import org.json.JSONPropertyName;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "zt_user")
@Data
@NoArgsConstructor
public class zt_user {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @JsonProperty("UserID")
    private String UserID;
    @JsonProperty("AuthID")
    private String AuthID;
    @JsonProperty("AuthName")
    private String AuthName;
    private  String ufzhangtao;
    private  String ufpzhangtao;
    private  String mtype;
}
