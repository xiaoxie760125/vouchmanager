package com.sqds.ufdataManager.registory.ufdata;

import com.sqds.ufdataManager.model.ufdata.Gl_mend;
import com.sqds.ufdataManager.registory.DataDml.ufdatabasebasic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface Gl_mendRepository  extends JpaRepository<Gl_mend,Integer> {
    /**
     * 取得没有结账的最大月份
     * @return
     */
    @Query("select max(gm.period) from Gl_mend gm where  gm.bflag=true")
    Integer MaxPeriodIsbFlag(ufdatabasebasic info);
}
