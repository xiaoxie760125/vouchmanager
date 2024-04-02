package com.sqds.springjwtstudy.controller;


import com.sqds.springjwtstudy.hentity.responser;
import com.sqds.springjwtstudy.jwtutil.modal.AuthRequest;
import com.sqds.springjwtstudy.jwtutil.modal.JwtService;
import com.sqds.anthdatamanange.data.User;
import com.sqds.anthdatamanange.data.zt_user;
import com.sqds.anthdatamanange.data.zt_userRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/auth")
public class AuthonController {
    @Autowired
    private JwtService authonService;
    @Autowired
    private zt_userRepository zt_userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private com.sqds.springjwtstudy.service.dataservice dataservice;
    @PostMapping("/login")
    public ResponseEntity<responser> authentiacateAndGetToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication =
                this.authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            try {
                  log.info(authentication.getName()+"登录成功");

                return  ResponseEntity.ok( new responser(200,this.authonService.genetateToken(authRequest.getUsername())));
            } catch (Exception e) {
                log.info(authRequest.getUsername()+"登录失败("+e.getMessage());
                return  ResponseEntity.ofNullable(new responser(500,e.getMessage()));

            }
        } else {
           throw new UsernameNotFoundException("user is null or password is error");
        }

    }
    @GetMapping("/hellowjwt")
    public  ResponseEntity<responser> hellow()
    {
        /*SecurityContext securityContext= SecurityContextHolder
                .getContext();
        Authentication authentication=securityContext.getAuthentication();
        if(authentication!=null)
        {
            User uesr=(User)authentication.getPrincipal();
            System.out.println(uesr.getUsername());
            System.out.println(uesr.getPassword());
        }*/
        return  ResponseEntity.ofNullable(new responser(200,"hellow"));
    }
    @PostMapping("/hellow/{name}")
    public ResponseEntity<responser> hellowworld(@PathVariable String name)
    {
        return     ResponseEntity.ofNullable(new responser(200,"hellow"+name));
    }
    @GetMapping("/getalluser")
    public ResponseEntity<responser> getalluser() {
        var user = this.dataservice.findAll();
        System.out.println(user);
        return ResponseEntity.ofNullable(new responser(200, user));
    }
    @PostMapping("/validtoken")
    public  Boolean isvalidtoken(@RequestBody String token)
    {
        try {
            return authonService.isTokenExpired(token);
        } catch (Exception e) {
            return  false;

        }
    }
    /**
     *得到用户的辅助信息
     * @param id
     * @return
     */
    @GetMapping("/getuserinfo/{id}")
    public  responser<List<zt_user>> userinfo(@PathVariable int id)
    {
         List<zt_user> userinfos=zt_userRepository.getzt_userByUserid(id);
         return  new responser<List<zt_user>>(200,userinfos);
    }
    @PostMapping("/adduser")
    public  ResponseEntity<responser> addser(@RequestBody User user)
    {
        PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        this.dataservice
                .save(user);
        return ResponseEntity.ofNullable(new responser(200,"hellow"));
    }
    @PostMapping("/adduserinfo")
    public  ResponseEntity<responser> adduserinfo(@RequestBody zt_user zt_user)
    {
        try
        {
        zt_userRepository.save(zt_user);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

        return ResponseEntity.ofNullable(new responser(200,"hellow"));
    }


}
