package com.sqds.ufdataManager.model.ufdata;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name ="GL_accass")
public class Gl_accass {
   @Id
   private  int i_id;
   private  String ccode;
   private  int iperiod;
   private String cbegind_c;
   private  String cbegind_c_engl;
   private  String cendd_c;
   private  String cendd_c_engl;
   private BigDecimal mc;
   private  BigDecimal md;
   private  BigDecimal mb;
   private  BigDecimal me;
   private  String  cdept_id;
   private String csup_id;
   private String cperson_id;
   private String ccus_id;
   private  String citem_id;
   private String citem_class;



}
