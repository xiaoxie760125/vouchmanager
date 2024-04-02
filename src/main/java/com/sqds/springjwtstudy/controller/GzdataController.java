package com.sqds.springjwtstudy.controller;

import com.sqds.springjwtstudy.controller.responsemodel.FGzUf;
import com.sqds.springjwtstudy.controller.responsemodel.GzModel;
import com.sqds.springjwtstudy.controller.responsemodel.GzUfModel;
import com.sqds.springjwtstudy.hentity.responser;
import com.sqds.ufdataManager.model.ufdata.Department;
import com.sqds.ufdataManager.model.ufdata.Gl_Accvouch;
import com.sqds.ufdataManager.model.ufdata.Wa_GZData;
import com.sqds.ufdataManager.registory.DataDml.ufdatabasebasic;
import com.sqds.ufdataManager.registory.ufdata.DepartmentRepository;
import com.sqds.ufdataManager.registory.ufdata.Gl_AccVouchRepository;
import com.sqds.ufdataManager.registory.ufdata.Wa_GZDataRepository;
import com.sqds.ufdataManager.registory.ufdata.vouchcontioan;
import com.sqds.ufdataManager.registory.ufsystem.Ua_periodRepository;
import com.sqds.vouchandufdataservice.Insertufdatavouchs;
import com.sqds.vouchandufdataservice.axinfo;
import com.sqds.vouchandufdataservice.resport;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("gzdata")
public class GzdataController {
    @Autowired
    Wa_GZDataRepository gzDataRepository;
   @Autowired()
   DepartmentRepository departmentRepository;
    @Autowired
    Ua_periodRepository periodRepository;
   @Autowired
    Insertufdatavouchs insertvouchs;
   @Autowired
    Gl_AccVouchRepository glAccVouchRepository;
   @PostMapping("/getcolumns/{ufzhangtaohao}")
    public responser<List<axinfo>> getcolumns(@RequestBody() String[] columns, @PathVariable("ufzhangtaohao") String ufzhangtaohao) throws InvocationTargetException, IllegalAccessException {ufdatabasebasic info=new ufdatabasebasic(ufzhangtaohao);

        List<axinfo> result=this.gzDataRepository.getcolums(columns,true, info);
        return  new responser<>(200,result);

    }
    @PostMapping("savereportedit/{zhangtaohao}")
    public  responser<String> savereportedit(@RequestBody() List<axinfo> gzData, @Param("reportname") String reportname,@Param("isgroup") boolean isgroup, @PathVariable("zhangtaohao") String zhangtaohao,@Param("columns")String[] columns) throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        this.insertvouchs.setgzreport(zhangtaohao,gzData,reportname,isgroup,columns);
        return  new responser<>(200,reportname);
    }
    @GetMapping("getreportedit/{zhangtaohao}")
    public  responser<List<resport<axinfo>>> getreportedit(@PathVariable("zhangtaohao") String zhangtaohao) throws NoSuchFieldException, IllegalAccessException {
        List<resport<axinfo>> reports= this.insertvouchs.getgzreport(zhangtaohao);
        return  new responser<>(200, reports);
    }

    /**
     * 保存ufmodel
     * @param ufmodel
     * @param ufzhangtaohao
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
@PostMapping("saveufmodel/{ufzhangtaohao}")
    public  responser<String> saveufmodel(@PathVariable("ufzhangtaohao") String ufzhangtaohao,@RequestBody() GzUfModel ufmodel) throws InvocationTargetException, IllegalAccessException
    {
        this.insertvouchs.setgzufmode(ufzhangtaohao,ufmodel);
        return  new responser<>(200,"数据已保存");
    }

    /**
     * 获取工资凭证列表
     * @param ufzhangtaohao
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @GetMapping("getgzmodel/{ufzhangtaohao}")
    public  responser<List<GzUfModel>> getgzmodel(@PathVariable("ufzhangtaohao") String ufzhangtaohao) throws InvocationTargetException, IllegalAccessException
    {
        List<GzUfModel> gzUfModels=this.insertvouchs.getgzufmode(ufzhangtaohao);
        return  new responser<>(200,gzUfModels);
    }
    /**
     *
     * @param vc
     * @param zhangtaohao
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @PostMapping("/getwadata/{zhangtaohao}")
    public responser<List<Wa_GZData>> getwadata(@RequestBody() vouchcontioan vc, @PathVariable("zhangtaohao") String zhangtaohao,@Param("columns") String[] columns) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        //dateתlocaldate

        ufdatabasebasic info=new ufdatabasebasic(zhangtaohao);
        info.setYear(vc.getBegindate().getYear());
        List<Wa_GZData> gzData=this.gzDataRepository.FindGzinfoIsum(vc,columns,info);
        return  new responser<>(200,gzData);
    }
    @GetMapping("getdep/{ufzhangtaohao}")
    public responser<List<Department >> getdep(@PathVariable("ufzhangtaohao") String ufzhangtaohao,@Param("depcode")String depcode) {
        try {



        ufdatabasebasic info=new ufdatabasebasic(ufzhangtaohao);
        depcode=depcode.equals("null")?"":depcode;
        List<Department> dep=this.gzDataRepository.getdeptment(depcode,info);
        return  new responser<>(200,dep);}
        catch (Exception e) {
         return  null;
        }
    }
    /**
     * ��ǰ�����ع������б�
     * @param gzdata
     * @return
     * @throws IOException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @PostMapping("exportgzdata/{ufzhangtaohao}")
    public ResponseEntity<byte[]>  exportgzdata(@RequestBody GzModel<Object> gzdata, @PathVariable String ufzhangtaohao) throws IOException, InvocationTargetException, IllegalAccessException {
        try {
            String filename="gzdata"+UUID.randomUUID().toString()+".xlsx";;
            File tempfile=new File(filename);
            FileOutputStream fileOutputStream=new FileOutputStream(tempfile);
            ufdatabasebasic info=new ufdatabasebasic(ufzhangtaohao);
            Workbook excelbook=this.gzDataRepository.getworkbook(gzdata.getVouchs(),gzdata.getModels(),info);
            excelbook.write(fileOutputStream);
            Path path= Paths.get(filename);

            Resource resource=new InputStreamResource(Files.newInputStream(path));
            byte[] files=resource.getInputStream().readAllBytes();
            HttpHeaders headers=new HttpHeaders();
            headers.add("Content-Disposition","attachment;filename="+filename);
            headers.add("content-type","application/octet-stream");
            headers.add("Cache-Control","no-cache,no-store,must-revalidate");
            headers.add("Pragma","no-cache");

            var reponse=ResponseEntity.ok().headers(headers).body(files);
            if(tempfile.delete())
            {
                System.out.println("�ļ�ɾ���ɹ�");
            }
            return reponse;



        }
        catch (Exception e)
        {
            return  null;
        }

    }
    @PostMapping("updategzdatafromexcel/{zhangtaohao}")
    public responser<List<Wa_GZData>> updategzdatafromexcel(@RequestParam("file")MultipartFile file,@PathVariable() String zhangtaohao) throws InvocationTargetException, IllegalAccessException, IOException, InstantiationException {
        String filename=file.getOriginalFilename();
        String[] filenameArr=filename.split("\\.");
        Path excelfiles= Files.createTempFile(UUID.randomUUID().toString(),filenameArr[1]);
        file.transferTo(excelfiles);
        ufdatabasebasic info=new ufdatabasebasic(zhangtaohao);
        vouchcontioan message=  this.gzDataRepository.insertgzdatafromExcelFile(excelfiles.toFile(),info);
        if(message!=null)
        {
            List<String> formula=this.gzDataRepository.computerFormula(message,info);
            if(formula.size()>1) {
                List<Wa_GZData> list = this.gzDataRepository.FindGzinfoIsum(message, new String[]{}, info);
                return  new responser<>(200,list);
            }

            return  new responser<>(500,new LinkedList<>());


        }
        else
        {
            return  new responser<>(500,new LinkedList<>());
        }


    }
    @PostMapping("createuf/{zhangtaohao}")
    public  responser<String>  updategzdatafromexcel(@RequestBody FGzUf model, @PathVariable("zhangtaohao") String zhangtaohao) {
        try {
            ufdatabasebasic info=new ufdatabasebasic(zhangtaohao);
           List<Gl_Accvouch> gl_accvouchs=this.gzDataRepository.GetGzUfDalModel(model
                   .getVc(),model.getModel(),info);
           gl_accvouchs.forEach(
                   s->{s.setCbill(model.getBill());
                       s.setIdoc(model.getIdoc());}
           );
           info.setZhangtaohao(model.getModel().getVouchmanager().getUfzhangtao());
           String pingzhao=this.glAccVouchRepository.Insertvouch(info,gl_accvouchs,model.isIsfirst());
           return new responser<>(200,pingzhao);

        } catch (
                Exception e
        ) {

            return new responser<>(500,e.getMessage());

        }

    }
    @PostMapping("computer/{zhangtaohao}")
    public  responser<List<Wa_GZData>>  computer(@RequestBody vouchcontioan model, @PathVariable("zhangtaohao") String zhangtaohao) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        List<String> formulas=this.gzDataRepository.computerFormula(model,new ufdatabasebasic(zhangtaohao));
        if(formulas.size()>1)
        {
            List<Wa_GZData> gzdata=
                    this.gzDataRepository.FindGzinfoIsum(model,new String[]{}, new ufdatabasebasic(zhangtaohao));

            return new responser<>(200,gzdata);
        }
        else
        {
            return new responser<>(500,new LinkedList<>());
        }

    }



}
