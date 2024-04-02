package com.sqds.ufdataManager.registory.ufdata;

import com.sqds.ufdataManager.model.ufdata.Wa_GZData;
import com.sqds.ufdataManager.model.ufdata.keymodel.wa_gzdatakey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Wa_GZDataRepository extends JpaRepository<Wa_GZData, wa_gzdatakey>,Wa_GZDataCustomerRepositoy{
}
