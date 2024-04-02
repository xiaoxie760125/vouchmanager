package com.sqds.vouchdatamanager.registroy;

import com.sqds.vouchdatamanager.model.bankinf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface bankinfRepository
extends JpaRepository<bankinf,String>
{

    @Query("select  v from bankvouchnote   v where  v.bankcode=?1")
    bankinf findFristByBankcode(String banckcode, String database);
}
