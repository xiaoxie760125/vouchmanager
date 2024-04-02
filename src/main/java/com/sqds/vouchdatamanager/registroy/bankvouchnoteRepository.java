package com.sqds.vouchdatamanager.registroy;


import com.sqds.vouchdatamanager.model.bankvouchnote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface bankvouchnoteRepository extends JpaRepository<bankvouchnote,String>,banknodeCustomerRository {

}
