package com.sqds.vouchdatamanager.model;

public enum  vouchtype
{
    basic("增值税专用发票"),
    commen("增值税普通发票");
    private final  String type;

    private  vouchtype(String  type)
    {
        this.type=type;
    }



}
