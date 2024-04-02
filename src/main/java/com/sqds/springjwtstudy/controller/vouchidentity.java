package com.sqds.springjwtstudy.controller;

import com.sqds.springjwtstudy.hentity.responser;
import com.sqds.vouchshiebie.baiduvouchs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("imagedata")
public class vouchidentity {

    @Autowired
    baiduvouchs vouchidentity;
    @PostMapping("getdatafromimage")
    public responser<String> getdatafromimage(@RequestBody String imagedata) throws Exception {
        return  new responser<>(200,this.vouchidentity.vouchinfrombase64(imagedata));
    }

}
