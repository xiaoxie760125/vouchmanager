package com.sqds.springjwtstudy.controller.responsemodel;

import com.sqds.ufdataManager.model.ufdata.code;
import com.sqds.vouchandufdataservice.axinfo;
import com.sqds.vouchandufdataservice.ufvouchsumtype;
import com.sqds.vouchdatamanager.model.Department;
import com.sqds.vouchdatamanager.model.vouchmanager;
import lombok.Data;

import java.util.List;

@Data
public class GzUfModel {
    private  String key;
    private  String name;
    private ufvouchsumtype ufmy;
    //工资凭证对应分配部门
    private List<Department> departments;
    //总账对应的部门
    private  List<Department> ufdepartments;
    //工资凭证对应的列影响
    private  List<axinfo> columns;
    private  vouchmanager vouchmanager;
    //借方凭证
    private code mdcode;
    //贷方号码
    private  code mccode;

}
