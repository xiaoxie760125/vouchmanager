package com.sqds.ufdataManager.registory.ufsystem;

import com.sqds.ufdataManager.model.ufsystem.Ua_period;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface Ua_periodRepository extends JpaRepository<Ua_period,String> {
     /*
      * 查询当前用户最大账套年度
      */
    @Query("select max(uap.iYear) from Ua_period uap where uap.cAcc_id=?1")
    Integer findMaxPeriodByCAcc_id(String CAcc_id);
}
