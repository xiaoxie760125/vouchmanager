package com.sqds.vouchdatamanager.registroy;

import com.sqds.vouchdatamanager.model.CustomerPerson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerPersonRepository extends JpaRepository<CustomerPerson,String> {
}
