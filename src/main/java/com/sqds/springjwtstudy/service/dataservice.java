package com.sqds.springjwtstudy.service;

import com.sqds.anthdatamanange.data.User;
import com.sqds.anthdatamanange.data.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class dataservice implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      Collection<SimpleGrantedAuthority> grantedAuthorityCollection=new ArrayList<>();
      var nuser=this.userRepository.findByUsernameIs(username);
      if(nuser.isPresent())
      {
          grantedAuthorityCollection.add(new SimpleGrantedAuthority(nuser.get().getRoleid()));
          return new org.springframework.security.core.userdetails.User(nuser.get().getUsername(),nuser.get().getPassword(),grantedAuthorityCollection);
      }
        return null;
    }

    public List<User> findAll() {
        var users= userRepository.findAll().stream().map(s->{
           User c=new User();
           c.setId(s.getId());
           c.setRoleid(s.getRoleid());
           c.setUsername(s.getUsername());
           return  c;
        });


        return users.collect(Collectors.toList());
    }

    public void save(User user) {
        this.userRepository.adduser(user.getUsername(),user.getPassword(),user.getRoleid());
    }
}
