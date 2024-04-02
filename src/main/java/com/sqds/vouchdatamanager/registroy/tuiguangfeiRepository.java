package com.sqds.vouchdatamanager.registroy;

import com.sqds.vouchdatamanager.model.tuiguangfei;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface tuiguangfeiRepository extends JpaRepository<tuiguangfei,String> {
    @Query("select v from  tuiguangfei  v where v.awartypename=?1")
    tuiguangfei findByMname(String mname,String database);
}
