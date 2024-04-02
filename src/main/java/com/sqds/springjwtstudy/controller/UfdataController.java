package com.sqds.springjwtstudy.controller;

import com.sqds.filelutil.excelutil.excekbook;
import com.sqds.springjwtstudy.controller.responsemodel.contion;
import com.sqds.springjwtstudy.controller.responsemodel.vouchgl;
import com.sqds.springjwtstudy.hentity.responser;
import com.sqds.ufdataManager.model.ufdata.Gl_Accvouch;
import com.sqds.ufdataManager.model.ufdata.code;
import com.sqds.ufdataManager.model.ufdata.person;
import com.sqds.ufdataManager.registory.DataDml.Gl_AccVouchRepositoryHelp;
import com.sqds.ufdataManager.registory.DataDml.GlvouchClient;
import com.sqds.ufdataManager.registory.DataDml.ufdatabasebasic;
import com.sqds.ufdataManager.registory.ufdata.vouchcontioan;
import com.sqds.vouchandufdataservice.Basicinfo;
import com.sqds.vouchandufdataservice.Insertufdatavouchs;
import com.sqds.vouchandufdataservice.MutiSerivce;
import com.sqds.vouchandufdataservice.vouchtoglmodel;
import com.sqds.vouchdatamanager.model.newsvouchs;
import com.sqds.vouchdatamanager.model.vouchmanager;
import com.sqds.vouchdatamanager.registroy.vouchmanagerRepository;
import lombok.Getter;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ufdata")
public class UfdataController {
    @Autowired
    Insertufdatavouchs ufdataservice;
    @Autowired
    MutiSerivce vouchandufdataservice;
    @Getter
    @Autowired
    Basicinfo basicinfoservice;
    @Autowired
    Insertufdatavouchs radisServce;
    @Autowired
    vouchmanagerRepository vouchmanager;
    @Autowired
    excekbook  excelutil;
    @Autowired
    private GlvouchClient glservice;
    @PostMapping("/shoukuanbymodel")
    public ResponseEntity<String> insertufdatavouchs(@RequestBody List<newsvouchs> vouchlist,
            @RequestBody vouchtoglmodel vouchmodel) {
        // 入参前对未收款订单进行过滤
        List<newsvouchs> s = vouchlist.stream().filter(sr -> {
            return sr.getShishou() == BigDecimal.valueOf(0);
        }).collect(Collectors.toList());
        return ResponseEntity.ok(vouchandufdataservice.insertvouch(s, vouchmodel, false));
    }

    @PostMapping("/getcode")
    public responser<List<code>> codes(@RequestBody ufdatabasebasic info) {
        return new responser<List<code>>(200, ufdataservice.getCodeList(info, info.getCode()));
    }

    @PostMapping("/axinfo")
    public responser<Map<String, List<Gl_AccVouchRepositoryHelp.axinfo>>> getaxinfo(@RequestBody ufdatabasebasic info) {
        return new responser<Map<String, List<Gl_AccVouchRepositoryHelp.axinfo>>>(200,
                ufdataservice.getcodeinfo(info, info.getCode()));
    }

    @GetMapping("/vouchmanager/{year}/{vmname}")
    public responser<List<vouchmanager>> getvouchmananger(@PathVariable("year") String year,
            @PathVariable("vmname") String vmname) {

        List<vouchmanager> vs = basicinfoservice.getvouchmanangerlist(Integer.parseInt(year), vmname);
        return new responser<List<vouchmanager>>(200, vs);
    }
    @PostMapping("dynamicglvouch/{zhangtaohao}")
    public  responser<List<Gl_Accvouch>> dynamicvouchs(@PathVariable("zhangtaohao")String zhangtaohao, @RequestBody vouchcontioan vc)
    {
       ufdatabasebasic info=new ufdatabasebasic(zhangtaohao);
       info.setYear(vc.getBegindate().getYear());
       List<Gl_Accvouch> vouchs=ufdataservice.dymicglvouchs(info,vc);
       return  new  responser<List<Gl_Accvouch>>(200,vouchs);

    }

    @PostMapping("/vmm")
    public responser<List<vouchmanager>> getvouchmananger(@RequestBody ufdatabasebasic info) {

        List<vouchmanager> vs = basicinfoservice.getvouchmanangerlist(info.getYear(), info.getZhangtaohao());
        return new responser<List<vouchmanager>>(200, vs);
    }

    @PostMapping("/getvmperson")
    public responser<List<person>> getvmperson(@RequestBody ufdatabasebasic info) {
        List<person> vs = basicinfoservice.getpersonlist(info.getCode(), info);

        return new responser<List<person>>(200, vs);
    }

    /**
     * 将转项凭证设置写入redis缓存
     * 
     * @param modelkey
     * @param vouchtoglmodel
     * @return
     */
    @PostMapping("/updateufmodel")
    public responser<String> updateufmodel(@Param("modelkey") String modelkey,
            @RequestBody vouchtoglmodel vouchtoglmodel) {
        try {

            this.radisServce.setvouchglmodel(modelkey, vouchtoglmodel);
            this.vouchmanager.addvouchmanager(vouchtoglmodel.getVouchmanager(), vouchtoglmodel.getYear() + "");
            return new responser<String>(200, "保持凭证设置");
        } catch (Exception e) {
            return new responser<String>(500, e.getMessage());
        }
    }

    @GetMapping("/glmodel")
    public responser<List<vouchtoglmodel>> getvouchglmodel(@Param("modelkey") String modelkey,@Param("vmname") String vmname) throws NoSuchFieldException, IllegalAccessException
    {
        List<vouchtoglmodel> vs=this.radisServce.getvouchmanagerlist(modelkey, vmname);
        return new responser<List<vouchtoglmodel>>(200, vs);
    }

    /**
     * 从缓存中读取默认凭证设置
     * 
     * @param modelkey
     * @param vouchs
     * @return
     */
    @PostMapping("/shoukuan")
    public responser<String> updatatevouchAndToUfvouch(@Param("modelkey") String modelkey, @Param("bill") String bill,
            @Param("jiesuan") boolean isjiesuan, @RequestBody List<newsvouchs> vouchs) {
        vouchtoglmodel vouchtoglmodel = this.radisServce.getvouchmanager(modelkey);
        if (vouchtoglmodel == null) {
            return new responser<String>(500, "没有设置过凭证");
        }
        try {
            if (bill != null)
                vouchtoglmodel.setCbill(bill);
            String insersult = this.vouchandufdataservice.insertvouch(vouchs, vouchtoglmodel, isjiesuan);
            return new responser<String>(200, insersult);

        } catch (Exception e) {
            return new responser<String>(500, e.getMessage());
        }

    }
    @PostMapping("/jiesuan")
    public  responser<String> updatevouchandtovoucufjiesuan(@RequestBody vouchgl<newsvouchs,vouchtoglmodel> vouchgl,@Param("bill") String bill,
                                                            @Param("jiesuan") boolean isjiesuan)
    {
            if(vouchgl.getModels()==null)
            {
                return new responser<String>(500, "没有凭证数据");
            }
            try
            {
                if(vouchgl.getModels().getCbill()==null)
                {
                    vouchgl.getModels().setCbill(bill);
                }
                String insersult = this.vouchandufdataservice.insertvouch(vouchgl.getVouchs(),vouchgl.getModels(),isjiesuan);
                return  new responser<String>(200,insersult);
            }
            catch (Exception e)
            {
                return  new responser<String>(200,e.getMessage());
            }

    }

    @GetMapping("/getufmodel")
    public responser<vouchtoglmodel> ufmodel(@Param("modelkey") String modelkey) {
        vouchtoglmodel vouchtoglmodel = this.radisServce.getvouchmanager(modelkey);
        return new responser<vouchtoglmodel>(200, this.radisServce.getvouchmanager(modelkey));
    }

    @PostMapping("/canclevouchs")
    public responser canclevouchs(@Param("isshoukuan") boolean isshoukuan, @Param("year") String year,
            @RequestBody List<newsvouchs> newsvouchsup) {
        int status = this.vouchandufdataservice.cancevouchs(newsvouchsup, year, isshoukuan);
        String message = status == 200 ? "数据已更新" : "系统错误";
        return new responser(status, message);

    }
    @PostMapping("/excelexport")
    public  ResponseEntity<byte[]>  excelexport(@RequestBody contion<Gl_Accvouch> vouchtoglmodels) throws IOException, IllegalAccessException {
        Workbook book=this.excelutil.workbook(vouchtoglmodels.getNewsvouchs(),vouchtoglmodels.getColumns(),Gl_Accvouch.class);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        book.write(os);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "vouchtoglmodel.xlsx");
        return new ResponseEntity<>(os.toByteArray(), headers, HttpStatus.CREATED);

    }

    @PostMapping("/getvouchs/{ufzhangtao}")
    public responser<List<Gl_Accvouch>> getvouchs(@RequestBody vouchcontioan vc,@PathVariable("ufzhangtao") String ufzhangtao) {
       ufdatabasebasic info=new ufdatabasebasic(ufzhangtao);
       info.setYear(vc.getBegindate().getYear());
       List<Gl_Accvouch> vouchs=this.glservice.findvouchs(vc.getBegindate().getMonthValue(),vc.getEnddate().getMonthValue(),info,vc.getIno_id());
       return   new responser<>(200,vouchs);

    }
    @PostMapping("/getvouchino/{ufzhangtao}")
    public responser<Map<Integer,Integer>> getvouchino(@RequestBody vouchcontioan vc, @PathVariable("ufzhangtao") String ufzhangtao) {

  ufdatabasebasic info=new ufdatabasebasic(ufzhangtao);
        info.setYear(vc.getBegindate().getYear());
    Map<Integer,Integer> vouchs=this.glservice.getinum(vc.getBegindate(),vc.getEnddate(),info);
        return   new responser<>(200,vouchs);

    }
}
