package com.sqds.springjwtstudy.controller;

import com.sqds.springjwtstudy.hentity.responser;
import com.sqds.ufdataManager.registory.DataDml.personhelp;
import com.sqds.vouchdatamanager.model.bankinf;
import com.sqds.vouchdatamanager.model.bankvouchnote;
import com.sqds.vouchdatamanager.registroy.bankvouchnoteRepository;
import com.sqds.vouchdatamanager.registroy.vouchcontion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/banknode")
@Slf4j
public class banknodeContoller {

;
    @Autowired
    bankvouchnoteRepository bankvouchnoteRepository;
    @Autowired
    personhelp personinfo;
    @PostMapping("/updatenode/{database}")
    public  int updatenode(@PathVariable("database") String database,@RequestBody bankvouchnote banknote)
    {

        return  bankvouchnoteRepository.insertorupdate(banknote,database);
    }
    @PostMapping("/getnode/{database}")
    public List<bankvouchnote> getvouchnode(@PathVariable("database") String database,@Param("ufzhangtao") String ufzhangtao, @RequestBody vouchcontion c) {
       List<bankvouchnote> result = bankvouchnoteRepository.getbankvouchnode(c,ufzhangtao, database);
       return  result;

    }
    @GetMapping("/getbankinfo/{database}")
    public responser<bankinf> getbankinfo(@PathVariable("database")String database, @Param("zhangtaohao")String zhangtaohao, @Param("name")String name)
    {
        bankinf res= bankvouchnoteRepository.getbankinfo(name,zhangtaohao,database);
        return  new responser<>(200,res);
    }


}
