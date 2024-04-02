package com.sqds.vouchdatamanager.registroy;

import com.sqds.vouchdatamanager.model.vouchmanager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface vouchmanagerRepository extends JpaRepository<vouchmanager,String> ,vmCustomer{

    @Query("SELECT vm FROM vouchmanager vm WHERE vm.vmcode=?1")
    vouchmanager findFirstByVmcode(String vouchId,String datasource);



}
