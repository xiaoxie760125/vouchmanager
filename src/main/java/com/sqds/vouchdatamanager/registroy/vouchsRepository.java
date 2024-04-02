package com.sqds.vouchdatamanager.registroy;

import com.sqds.vouchdatamanager.model.vouchs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface vouchsRepository  extends JpaRepository<vouchs,String>,vouchsCustomerRepository{
}
