package com.sqds.ufdataManager.registory.ufsystem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.sqds.ufdataManager.model.ufsystem.Ua_Account;
import lombok.Data;
import java.util.List;
public interface Ua_AccountRepository
extends JpaRepository<Ua_Account,String> {


    @Query("select u from Ua_Account  u where u.cAcc_Id=?1")
    Ua_Account finFirstaccount(String account);
    @Query("select u from Ua_Account u")
    List<Ua_Account> findAllAccount();
}

