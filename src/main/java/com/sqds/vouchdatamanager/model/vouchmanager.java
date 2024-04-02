package com.sqds.vouchdatamanager.model;

import com.sqds.ufdataManager.registory.DataDml.Gl_AccVouchRepositoryHelp.axinfo;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "vouchmanager")
@Data
public class vouchmanager {
    @Id
    private String vmcode;
    private  String vmname;
    private  String ufzhangtao;
    private  String mccode;
    private  String mdcode;
    private  double taxvalue;
    private  String mdtaxcode;
    private  String  mctaxcode;
    private  String type;
    private String midccode;
    @Transient
    private axinfo mdaxselect;
    @Transient
    private axinfo mcaxselect;


}
