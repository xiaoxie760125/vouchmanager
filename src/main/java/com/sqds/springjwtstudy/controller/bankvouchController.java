package com.sqds.springjwtstudy.controller;

import com.sqds.filelutil.excelutil.excekbook;
import com.sqds.springjwtstudy.controller.responsemodel.vouchgl;
import com.sqds.springjwtstudy.hentity.responser;
import com.sqds.vouchandufdataservice.vouchtoglmodel;
import com.sqds.vouchdatamanager.model.bankvouchs;
import com.sqds.vouchdatamanager.registroy.bankvouchsRepository;
import com.sqds.vouchdatamanager.registroy.vouchcontion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("bankvouchs")
public class bankvouchController {
 @Autowired
 private bankvouchsRepository bankvouchsRepository;
 @Autowired
 private excekbook<bankvouchs> excekbook;
 @PostMapping("/insertbankvouchs/{year}")
 public responser<String> insertbankvouchs(@RequestBody List<bankvouchs> bankvouchs,@PathVariable String year) {
    Integer result=this.bankvouchsRepository.insertorupdatebankinfo(bankvouchs, year);
    if(result>0)
    {
    return new responser<String>(200,"数据更新");
    }
    else
    {
        return new responser<String>(400,"数据更新失败");
    }
     
 }
 @PostMapping("/uploadvouchs/{year}/{ufzhangtao}")
 public responser<List<bankvouchs>>  insertbankvouchs(@RequestParam("file") MultipartFile file,@PathVariable String year,@PathVariable String ufzhangtao)  {
    try {
         String filename=file.getOriginalFilename();
            String profix=filename.substring(filename.lastIndexOf("."));
            Path excelifle= Files.createTempFile(UUID.randomUUID().toString(),profix);
            file.transferTo(excelifle);
        List<bankvouchs> bankvouchs = excekbook.getDataList(excelifle.toFile(),bankvouchs.class);
        bankvouchs.forEach(s->s.setUfaccount(ufzhangtao));
        Integer result= this.bankvouchsRepository.insertorupdatebankinfo(bankvouchs, year);
       if(result>0)
       {
        return new responser<>(200,bankvouchs);
       }
       else
       {
        bankvouchs.clear();
         return new responser<>(200,bankvouchs);
       }
      
    } catch (InstantiationException | IllegalAccessException | IOException e) {
        // TODO Auto-generated catch block
        return new responser<>(200,new ArrayList<bankvouchs>());
    }
     
 }
 //更新银行凭证
 @PostMapping("updateglaccvouch/{year}")
 public responser<String>  updateglaccvouch(@RequestBody vouchgl<bankvouchs, vouchtoglmodel> vouchgl, @PathVariable String year)  {

     if(vouchgl.getVouchs().isEmpty())
     {
         return  new responser<>(500,"银行凭证数据为空");
     }
     return new responser<>(200,this.bankvouchsRepository.insertglvouchsfromvouchs(vouchgl, year));
     
 }
 @PostMapping("getvouchs/{year}")
 public responser<List<bankvouchs>>  getvouchs(@RequestBody vouchcontion searchcontion, @PathVariable String year)  {
    return new responser<>(200,this.bankvouchsRepository.getbankvouchs(searchcontion, year));
     
 }



}
