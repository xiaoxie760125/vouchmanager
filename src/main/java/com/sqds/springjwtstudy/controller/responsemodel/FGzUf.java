package com.sqds.springjwtstudy.controller.responsemodel;

import com.sqds.ufdataManager.registory.ufdata.vouchcontioan;
import lombok.Data;

@Data

public class FGzUf {
     private vouchcontioan vc;
     private GzUfModel model;
     private String bill;
     private int idoc;
     private boolean isfirst;

}
