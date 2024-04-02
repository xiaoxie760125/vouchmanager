package com.sqds.vouchdatamanager.registroy;

import com.sqds.springjwtstudy.controller.responsemodel.vouchgl;
import com.sqds.ufdataManager.model.ufdata.Gl_Accvouch;
import com.sqds.vouchandufdataservice.vouchtoglmodel;
import com.sqds.vouchdatamanager.model.bankvouchs;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface bankvouchsCustomerRepository {

    @Modifying
    @Transactional
    public  Integer insertorupdatebankinfo(List<bankvouchs> banks,String database);


   List<bankvouchs> getbankvouchs(vouchcontion vc,String database);

    public  List<Gl_Accvouch> getbankinfovouchs(List<bankvouchs> infos, vouchtoglmodel model, String database);
    String insertglvouchsfromvouchs(vouchgl<bankvouchs,vouchtoglmodel> vouchsresult,String database);
}
