package com.sqds.vouchdatamanager.registroy;

import com.sqds.vouchdatamanager.model.vouchmanager;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface vmCustomer {

    @Query("select  u from vouchmanager  u where u.ufzhangtao= ?1")
    List<vouchmanager> getvouchmanagerByufcode(@Param("ccode") String ccode,String datasource);
    vouchmanager addvouchmanager(vouchmanager vouchmanager,String database);


}
