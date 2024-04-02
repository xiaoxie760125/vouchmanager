package com.sqds.vouchdatamanager.model.idclass;

import lombok.Data;

import java.io.Serializable;

@Data
public class banvouchsid implements Serializable {
    private  String bankinfoid;
    private  String vouchno;
}
