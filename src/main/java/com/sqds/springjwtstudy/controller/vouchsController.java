package com.sqds.springjwtstudy.controller;

import com.sqds.filelutil.excelutil.excekbook;
import com.sqds.springjwtstudy.controller.responsemodel.vouchgl;
import com.sqds.springjwtstudy.controller.responsemodel.vouchsbody;
import com.sqds.springjwtstudy.hentity.responser;
import com.sqds.vouchandufdataservice.Insertufdatavouchs;
import com.sqds.vouchandufdataservice.vouchtoglmodel;
import com.sqds.vouchdatamanager.model.vouchs;
import com.sqds.vouchdatamanager.registroy.vouchcontion;
import com.sqds.vouchdatamanager.registroy.vouchsRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("pvouchs")
public class vouchsController {
    @Autowired
    excekbook<vouchs> excekbook;
    @Autowired
    vouchsRepository vouchs;
    @Autowired
    Insertufdatavouchs insertufdatavouchs;

    @GetMapping("updatetest/{year}/{vmname}")
    public responser<String> updatavouchtest(@PathVariable("year") String year,@PathVariable("vmname") String  vmnamearam)
    {
        return  new responser<>(200,year+vmnamearam);
    }

    @PostMapping("updateindentityvouchs/{year}/{vmname}")
    public  responser<List<vouchs>> updateindentityvouchs(@RequestBody() List<vouchs> nvouchs,@PathVariable("year") String year,@PathVariable("vmname") String  vmname)
    {
        for(var s:nvouchs)
        {
            s.setIsbuyer(s.getBuyername().contains(vmname));
            if(s.getVouchcode().isEmpty() && !s.getShudiancode().equals("--"))
            {
                s.setVouchcode(s.getShudiancode());
            }
        }
        this.vouchs.addvouchs(nvouchs,year);
        return  new responser<>(200, nvouchs);

    }

    @PostMapping("updatevouch/{year}/{vmname}")
    public ResponseEntity<List<vouchs>> updatevouch(@RequestParam("file") MultipartFile file, @PathVariable("year")String year, @PathVariable("vmname")String vmname, @Param("ufpzhangtap")String ufpzhangtao,
                                                        @Param("bill")String bill) throws InstantiationException, IllegalAccessException {
        List<vouchs> nvouchs=new LinkedList<>();
        try {
            String filename=file.getOriginalFilename();
            String profix=filename.substring(filename.lastIndexOf("."));
            Path excelifle= Files.createTempFile(UUID.randomUUID().toString(),profix);
            file.transferTo(excelifle);
            nvouchs=this.excekbook.getDataList(excelifle.toFile(),vouchs.class).stream().map(s->{
                s.setUfzhangtao(ufpzhangtao);
                s.setIsbuyer(s.getBuyername().contains(vmname));
                if(s.getVouchcode().isEmpty() && !s.getShudiancode().equals("--"))
                {
                    s.setVouchcode(s.getShudiancode());
                }

                return  s;
            }).collect(Collectors.toList());

            this.vouchs.addvouchs(nvouchs,year);



        } catch (IOException e) {

            return  new ResponseEntity<>(nvouchs,HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<List<vouchs>>(nvouchs, HttpStatus.OK);
    }

    @PostMapping("getpvouchs/{database}")
    public responser<List<vouchs>> getpvouchs(@RequestBody() vouchcontion contion,@PathVariable("database") String databse)
    {
        try {
            return  new responser<>(200,this.vouchs.getvouchbycontion(contion,databse));

        }
        catch (Exception E)
        {
            return  new responser<>(500,new LinkedList<>());
        }

    }
    @PostMapping("insertglvouchs/{database}")
    public   responser<String> getglvouhcsfromvouchs(@RequestBody() vouchgl<vouchs,vouchtoglmodel> vouchs, @PathVariable("database") String database)
    {
        


         String  result=this.insertufdatavouchs.addpvouchsofudata(vouchs.getVouchs(),vouchs.getModels());
        return  new responser<>(200,result);

    }
    @PostMapping("registervouchs/{database}")
    public responser<String> getvouchsbycontion(@RequestBody() vouchsbody body, @PathVariable("database") String database) throws Exception {

        try {
            Integer updatenewsvoush = this.vouchs.updatepersonp(body.getPerson(), body.getVs(), body.getInfo(), database);
            if (updatenewsvoush > 0) {

                return new responser<>(200, "发票已登记");
            } else {
                return new responser<>(500, "发票登记失败");
            }
        }
        catch (Exception E)
        {

            return new responser<>(500, "发票登记失败");
        }

    }

    /**
     *  vouchmodel
     */

     @Data
    public class  vouchmodel {
    
        private List<vouchs> vouchs;
        private vouchtoglmodel models;

        
    }



}
