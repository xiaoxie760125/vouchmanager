package com.sqds.ufdataManager.registory.ufdata;

import com.sqds.ufdataManager.model.ufdata.Gl_Accvouch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface Gl_AccVouchRepository extends JpaRepository<Gl_Accvouch,Integer> ,CustomerVouchManager{
/*
取得最大月的最大凭证号
 */
 @Query("select u.iperiod, max(u.ino_id) from  Gl_Accvouch  u where  u.iperiod=(" +
         "select  max(u1.iperiod) from  Gl_Accvouch  u1 where u1.iperiod between  1 and 12) group by u.iperiod")
 Integer[] maxinoidbyperiod();

}
