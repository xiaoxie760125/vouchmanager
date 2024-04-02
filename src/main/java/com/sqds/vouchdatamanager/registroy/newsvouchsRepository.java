package com.sqds.vouchdatamanager.registroy;

import com.sqds.vouchdatamanager.model.newsvouchs;
import org.springframework.data.jpa.repository.JpaRepository;


public interface newsvouchsRepository extends JpaRepository<newsvouchs,String>, newsvouchsmanager {


}
