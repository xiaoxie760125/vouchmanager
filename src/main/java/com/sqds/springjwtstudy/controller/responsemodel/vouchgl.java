package com.sqds.springjwtstudy.controller.responsemodel;

import lombok.Data;

import java.util.ArrayList;

@Data
public  class vouchgl<T,voucgltomodels> {

        private ArrayList<T> vouchs;

        private voucgltomodels models;
       

}
