package com.sqds.springjwtstudy.controller.responsemodel;

import com.sqds.vouchandufdataservice.resport;
import lombok.Data;

import java.util.List;
@Data
public class GzModel<T> {
     List<T> vouchs;
     resport models;

}
