package com.sqds.vouchdatamanager.registroy;

import com.sqds.ufdataManager.model.ufdata.Gl_Accvouch;
import com.sqds.vouchandufdataservice.axinfo;
import com.sqds.vouchandufdataservice.vouchtoglmodel;
import com.sqds.vouchdatamanager.model.vouchs;

import java.util.List;

public interface vouchsCustomerRepository  {
    Integer addvouchs(List<vouchs> vouchs,String database);
    List<vouchs> getvouchbycontion(vouchcontion vouchcontion,String database);
    List<Gl_Accvouch> fromvouchstogl(List<vouchs> vouchs, vouchtoglmodel model, String database);

    /**
     * 原始发票登记
     * @param p
     * @param v
     * @param info
     * @param database
     * @return
     * @throws Exception
     */
    Integer updatepersonp(String p, List<vouchs> v, axinfo info,String database) throws Exception;
}
