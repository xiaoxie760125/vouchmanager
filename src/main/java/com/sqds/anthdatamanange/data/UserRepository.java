package com.sqds.anthdatamanange.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface UserRepository  extends JpaRepository<User,Integer> {

    @Query("select u from User u where u.id = ?1")
    Optional<User> findByUsernameIs(String Username);

    @Query(value = "insert into  [User](username,password,roleid) values(:username,:password,:roleid)",nativeQuery = true)
    @Modifying
    void adduser(String username,String password,String roleid);
    @Query("select u.id, u.username,u.roleid from User  u")
    List<User>  findAllExcutePassword();

}
