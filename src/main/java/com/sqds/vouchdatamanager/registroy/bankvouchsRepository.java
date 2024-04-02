package com.sqds.vouchdatamanager.registroy;

import java.util.List;
import java.util.Optional;


import com.sqds.vouchdatamanager.model.bankvouchs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface bankvouchsRepository  extends JpaRepository<bankvouchs,Integer>,bankvouchsCustomerRepository{
    @Query("select  b from bankvouchs  b where b.vouchdate between  :#{#vc.begindate} and :#{#vc.enddate}" +
            " and (:#{#vc.customername} is NULL  or b.vouchcustname like concat('%',:#{#vc.customername},'%'))" +
            " and  b.ufaccount=:#{#vc.ufzhangtaohao}" +
            " and  (:#{#vc.vouch_type} is NUll or b.bankacccode=:#{#vc.vouch_type})")

    List<bankvouchs> findbankinfoBycontion(vouchcontion vc);
}

