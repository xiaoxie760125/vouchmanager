package com.sqds.anthdatamanange.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface zt_userRepository  extends JpaRepository<zt_user,Integer> {
    @Query("select u from zt_user u where u.UserID=?1")
    List<zt_user> getzt_userByUserid(int userid);
}
