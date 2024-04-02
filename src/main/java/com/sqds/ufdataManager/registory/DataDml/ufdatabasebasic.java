package com.sqds.ufdataManager.registory.DataDml;

import lombok.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Data
@Component("ufdatainfo")
public class ufdatabasebasic {

 
    public  ufdatabasebasic()
    {

    }
    public  ufdatabasebasic(String zhangtaohao)
    {
        this.zhangtaohao=zhangtaohao;

    }
    private  int year;
    private  String zhangtaohao;
    private  String code;
}
