package com.sqds.ufdataManager.registory.ufdata;

import com.sqds.ufdataManager.model.ufdata.person;
import com.sqds.ufdataManager.registory.DataDml.ufdatabasebasic;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface personCustomerRepository {
    /**
     * 动态工资人员信息
     * @param personname
     * @param info
     * @return
     */
    @Modifying
    @Transactional
   person dynamicgetgzperson(String personname, ufdatabasebasic info);

}
