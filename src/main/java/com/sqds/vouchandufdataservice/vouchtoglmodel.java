package com.sqds.vouchandufdataservice;

import com.sqds.ufdataManager.model.ufdata.Gl_Accvouch;
import com.sqds.vouchdatamanager.model.vouchmanager;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;


public class vouchtoglmodel extends Gl_Accvouch implements Serializable {


    public  vouchtoglmodel(){

    }
    @Getter
    @Setter
    private String key;
    @Getter
    @Setter
    private String name;
    public int getVouchyear() {
        return vouchyear;
    }

    public void setVouchyear(int vouchyear) {
        this.vouchyear = vouchyear;
    }

    public com.sqds.vouchdatamanager.model.vouchmanager getVouchmanager() {
        return vouchmanager;
    }

    public void setVouchmanager(com.sqds.vouchdatamanager.model.vouchmanager vouchmanager) {
        this.vouchmanager = vouchmanager;
    }

    public ufvouchsumtype getUfmy() {
        return ufmy;
    }

    public void setUfmy(ufvouchsumtype ufmy) {
        this.ufmy = ufmy;
    }

    public Map<String, com.sqds.vouchandufdataservice.axinfo> getAxinfo() {
        return this.axinfo;

    }

    public void setAxinfo(Map<String, com.sqds.vouchandufdataservice.axinfo> axinfo) {
        this.axinfo = axinfo;
    }
    //增加贷方科目的辅助核算项，以区别不同科目的辅助核算项目

    int vouchyear;
     vouchmanager vouchmanager;
     ufvouchsumtype ufmy;
     String vmname;

    public void setVmname(String vmname) {
        this.vmname = vmname;
    }
    public String getVmname(){
        return  this.vmname;
    }

    private  Map<String,axinfo> axinfo;
    //增加票据年度业务
    private  int year;
    public void  setYear(int year){
     this.year=year;
    }
    public   int getYear()
    {
        return  this.year;
    }
    private  String ufpzhangtao;
    public  String getUfpzhangtao()
    {
        return  ufpzhangtao;
    }
    public  void  setUfpzhangtao(String ufpzhangtao)
    {
        this.ufpzhangtao=ufpzhangtao;
    }
    private  Map<String,axinfo> mdaxinfo;
    public Map<String,axinfo> getMdaxinfo()
    {
        return  mdaxinfo;
    }
    public  void  setMdaxinfo(Map<String,axinfo> mdaxinfo)
    {
        this.mdaxinfo=mdaxinfo;
    }
    private Map<String,axinfo> mcaxinfo;
    public  Map<String,axinfo> getMcaxinfo()
    {
        return  mcaxinfo;
    }
    public  void  setMcaxinfo(Map<String,axinfo> mcaxinfo)
    {
        this.mcaxinfo=mcaxinfo;
    }
    @Getter
    @Setter
    Boolean isfirstvouch;
    @Override
    public boolean equals  (Object vouchmanager) {

        if(vouchmanager instanceof  vouchtoglmodel) {
            vouchtoglmodel vmob=(vouchtoglmodel) vouchmanager;
            return this.key.equals(vmob.key);
        }
        return  false;
    }
  


}
