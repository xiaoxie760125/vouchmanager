package com.sqds.vouchdatamanager.model;

public enum  vouchstatues
{
    basic("����"),
    error("�ѳ��"),
    cancle("������");

    private final String type;
     vouchstatues(String statustype)
     {
         type=statustype;
     }
}
