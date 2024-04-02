package com.sqds.vouchdatamanager.model;

import com.sqds.vouchdatamanager.registroy.PersonRepsitory;
import lombok.Data;

import java.util.Date;

@Data
public class baninfocon
{
    private  String customname;
    private Date begindate;
    private  Date enddate;

}
