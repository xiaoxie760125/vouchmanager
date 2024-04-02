package com.sqds.vouchdatamanager.model;

public enum  vouchstatues
{
    basic("正常"),
    error("已冲红"),
    cancle("已作废");

    private final String type;
     vouchstatues(String statustype)
     {
         type=statustype;
     }
}
