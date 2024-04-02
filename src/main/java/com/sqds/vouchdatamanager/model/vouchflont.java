package com.sqds.vouchdatamanager.model;

public enum  vouchflont
{
    basic("增值税发票管理平台"),
    common("电子发票服务平台");
    String type;
     vouchflont(String type)
    {
        this.type=type;
    }

}
