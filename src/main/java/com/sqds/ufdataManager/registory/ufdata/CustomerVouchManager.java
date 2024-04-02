package com.sqds.ufdataManager.registory.ufdata;

import com.sqds.ufdataManager.model.ufdata.GL_AccSum;
import com.sqds.ufdataManager.model.ufdata.Gl_Accvouch;
import com.sqds.ufdataManager.model.ufdata.code;
import com.sqds.ufdataManager.registory.DataDml.ufdatabasebasic;
import com.sqds.vouchdatamanager.model.vouchmanager;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Map;


public interface CustomerVouchManager {
    @Modifying
    String Insertvouch(ufdatabasebasic info, List<Gl_Accvouch> vouchs, Boolean isfirst);

    List<Gl_Accvouch> dymamicvouchs(ufdatabasebasic info, vouchcontioan vouch);
    <T> Map<String,T> getaxinfofromCode(ufdatabasebasic info,String code);

    GL_AccSum GetYue(ufdatabasebasic info, vouchcontioan ufvc);

    vouchmanager getvmbycode(ufdatabasebasic info, vouchmanager needvm);

    List<code> coderesult(ufdatabasebasic info, String contioan);

}
