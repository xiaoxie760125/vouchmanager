package com.sqds.vouchdatamanager.registroy;
import com.sqds.vouchdatamanager.model.bankinf;
import  com.sqds.vouchdatamanager.model.bankvouchnote;
import  com.sqds.ufdataManager.model.ufdata.person;

import java.util.List;

public interface banknodeCustomerRository {
    int insertorupdate(bankvouchnote bankinfo,String database);


    List<bankvouchnote> getbankvouchnode(vouchcontion voucontion, String zhangtaohao, String database);

    person getperson(String banckcode, String ufzhangtao, String database);

     bankinf getbankinfo(String banknode, String ufzhangtaohao, String databse);
}
