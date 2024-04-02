package com.sqds.ufdataManager.model.ufdata;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table

public class Gl_mend {
    @Id
    @Column(name ="iperiod")
    private  int period;
    private  boolean bflag;
    private  boolean bcheck;
    private  boolean bpri_check;
    private  boolean bflag_AP;
    private boolean isflag_AR;
    private boolean bflag_CA;
    private  boolean bflag_FA;
    private  boolean bflag_FD;
    private  boolean bflag_IA;
    private  boolean bflag_WA;




}
