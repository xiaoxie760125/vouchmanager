package com.sqds.springjwtstudy.controller;

import com.sqds.filelutil.excelutil.excekbook;
import com.sqds.fileutil.pdfutil.PdfUtil;
import com.sqds.springjwtstudy.controller.responsemodel.bodypara;
import com.sqds.springjwtstudy.controller.responsemodel.contion;
import com.sqds.springjwtstudy.hentity.responser;
import com.sqds.ufdataManager.model.ufdata.Gl_Accvouch;
import com.sqds.ufdataManager.model.ufdata.person;
import com.sqds.ufdataManager.registory.DataDml.ufdatabasebasic;
import com.sqds.ufdataManager.registory.ufdata.Gl_AccVouchRepository;
import com.sqds.ufdataManager.registory.ufdata.vouchcontioan;
import com.sqds.vouchdatamanager.Help.vouchcontion;
import com.sqds.vouchdatamanager.model.newsvouchs;
import com.sqds.vouchdatamanager.registroy.newsvouchsRepository;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class FileVouchControlerr {
    @Autowired
    excekbook<newsvouchs> excekbook;
    @Autowired
    newsvouchsRepository nesvouchsRepository;
    @Autowired
    PdfUtil pdfUtil;
    @Autowired
    Gl_AccVouchRepository glvouchs;

    /**
     * 服务端Excel数据
     */



    public  Path updatepvouchs(MultipartFile file) throws IOException {
            try {


                String filetname = file.getOriginalFilename();
                String profix = filetname.substring(filetname.lastIndexOf("."));
                Path excelifle = Files.createTempFile(UUID.randomUUID().toString(), profix);
                file.transferTo(excelifle);
                return  excelifle;
            }
            catch(Exception ex)
            {
                return  null;

            }


    }

    @PostMapping("updateprvouchs/{year}")
    public  ResponseEntity<String> updatprvouchs(@RequestBody()List<newsvouchs> newsvouchs,@PathVariable("year")String year)
    {
        try {

           var result= this.nesvouchsRepository.Insertorupdatevouchs(newsvouchs,year);
            return  new ResponseEntity<>("数据已保存",HttpStatus.OK);
        }
        catch (Exception e)
        {
            return  new ResponseEntity<>("系统错误",HttpStatus.BAD_REQUEST);

        }



    }

    @PostMapping("updatevouch/{year}/{vmname}")
    public ResponseEntity<List<newsvouchs>> updatevouch(@RequestParam("file")MultipartFile file, @PathVariable("year")String year,@PathVariable("vmname")String vmname,@Param("ufpzhangtap")String ufpzhangtao,
                                                        @Param("bill")String bill)
    {
        AtomicReference<Integer> result= new AtomicReference<>(0);
        List<newsvouchs> updatevouchs=new ArrayList<>();
        if(file.isEmpty())
        {
            return  new ResponseEntity<List<newsvouchs>>(updatevouchs,HttpStatus.NO_CONTENT);
        }
        else
        {

            try {
                String filename=file.getOriginalFilename();
                String profix=filename.substring(filename.lastIndexOf("."));




               Path excelfile=Files.createTempFile(UUID.randomUUID().toString(),profix);
                file.transferTo(excelfile);

                final String[] temppersoname = {""};

                   final    person[] tmpperson={new person()};
                //导入的数据列表中加入单位数据
                updatevouchs = this.excekbook.getDataList(excelfile.toFile(),newsvouchs.class).stream().map(newsvouchs ->{
                    newsvouchs.setMname(vmname);

                    //取得人员编码
                    if(!temppersoname[0].equals(newsvouchs.getCpsn_num()))
                    {
                        temppersoname[0]  =newsvouchs.getCpsn_num();
                         tmpperson[0]=this.nesvouchsRepository.getpersobyname(newsvouchs.getCpsn_num(),ufpzhangtao,year);


                    }
                    if(tmpperson[0].getCPsn_Name()!=null) {
                        newsvouchs.setCpsn_num(tmpperson[0].getCPsn_Num());
                        newsvouchs.setUfperson(tmpperson[0]);
                    }
                    newsvouchs.setJiesuanid("101");
                    //newsvouchs.setKaipiaoren(bill);
                    return newsvouchs;
                }).collect(Collectors.toList());
                if(updatevouchs.size()<=10)
                {
                  result.set(this.nesvouchsRepository.Insertorupdatevouchs(updatevouchs,year));
                }
                else
                {
                    ///没十条上传以此
                    int j=9;
                    for(int i=0;i<updatevouchs.size();i+=10)
                    {
                        List<newsvouchs> upchild=updatevouchs.subList(i,j);

                        Thread upthreA=new Thread(()->{
                           result.updateAndGet(v -> v + this.nesvouchsRepository.Insertorupdatevouchs(upchild,year));
                        });
                        j=j+10<updatevouchs.size()?j+10:updatevouchs.size();
                        upthreA.start();

                    }
                }

                 return  new ResponseEntity<List<newsvouchs>>(updatevouchs,HttpStatus.OK);

            }
            catch (Exception e)
            {
                newsvouchs erro=new newsvouchs();
                erro.setCustomername(e.getMessage());
                updatevouchs.add(erro);
                return  new ResponseEntity<List<newsvouchs>>(updatevouchs,HttpStatus.BAD_REQUEST);
            }


        }
    }
    private  FileInputStream inputStream(InputStream ins, File file)
    {
        try {

            OutputStream outputStream=new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            FileInputStream rs=new FileInputStream(file);
            outputStream.close();
            ins.close();
            return rs;
        }
        catch (Exception e)
        {
           return  null;
        }

    }

    private void inputStreamToFile(InputStream ins, File excelfile) {
        try
        {
            OutputStream outputStream=new FileOutputStream(excelfile);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            ins.close();
        }
        catch (Exception e)
        {

        }
    }
    @PostMapping("excelExport")
    public  ResponseEntity excelExport(@RequestBody contion<newsvouchs> vouch)
    {



        return  toexcelbook(vouch.getNewsvouchs(),vouch.getColumns());
    }

    /**
     * 导出到excel表
     * @param vouch
     * @return
     */
    @PostMapping("exportvouchs")
    public  ResponseEntity exportnewsvouchs(@RequestBody contion vouch)
    {
        return  toexcelbook(vouch.getNewsvouchs(),vouch.getColumns());
    }
    /**
     * 动态查询数据
     * @param year
     * @param c
     * @return
     */
    @PostMapping("getvouchlist/{year}")
    public responser<List<newsvouchs>> getnewsvouchs(@PathVariable("year") String year, @RequestBody vouchcontion c)
    {
        try {
            List<newsvouchs> vouchs = c.isIsyeji() ? this.nesvouchsRepository.getvouchallocation(c, year) : this.nesvouchsRepository.getvouchde(c, year);
            return new responser<List<newsvouchs>>(200,vouchs);
        }
        catch (Exception e)
        {
            return  new responser<>(500,null);

        }

    }
    @PostMapping("getcancleinfo/{year}")
    public  responser<List<newsvouchs>> getendjiesuan(@PathVariable("year")String year,@RequestBody vouchcontion c)
    {
        List<newsvouchs> jiesunmingxi=this.nesvouchsRepository.getvouchallocation(c,year);
        return  new responser<List<newsvouchs>>(200,jiesunmingxi);
    }
   @PostMapping("getsumvouch/{year}")
   public  responser<List<newsvouchs>> getsumvouch(@PathVariable("year") String year,@RequestBody vouchcontion c)
   {
       List<newsvouchs> vouchs=c.isIsyeji()?this.nesvouchsRepository.sumyejibyperson(c,year):this.nesvouchsRepository.vouchsum(c,year);
       return new responser<List<newsvouchs>>(200,vouchs);
   }

    /**
     * 前端汇总信息
     * @param year
     * @param begindate
     * @param enddate
     * @return
     */
   @GetMapping("getsumtext")
   public responser<Map<String,Object>> sumtext(@PathVariable("year") String year, @Param("begindate")LocalDateTime begindate,@Param("enddate")LocalDateTime enddate,@Param("mname")String manme)
   {
       Map<String,Object> map=this.nesvouchsRepository.sumtext(begindate,enddate,manme,year);
       return new responser<>(200,map);
   }
   @PostMapping("getsuminfo/{year}")
   public responser<Map<String,Object>> suminfo(@PathVariable("year") String year,@RequestBody vouchcontion vouchcontion)
   {
       LocalDateTime begindate=vouchcontion.getBegindate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
       LocalDateTime enddate=vouchcontion.getEnddate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
       Map<String,Object> map=this.nesvouchsRepository.sumtext(begindate,enddate,vouchcontion.getMname(),year);
       return new responser<>(200,map);
   }
    /**
     * 取得待分单列表
     * @param year
     * @param c
     * @return
     */
    @PostMapping("getfendan/{year}")
    public  responser<List<newsvouchs>> getfendan(@PathVariable("year") String year,@RequestBody vouchcontion c)
    {
        List<newsvouchs> vouchs=this.nesvouchsRepository.fendanlist(c,year);
        return new responser<>(200, vouchs);
    }

    /**
     * 前端分单API
     * @param year
     * @param ncc
   
     * @return
     */
    @PostMapping("fendan/{year}")
    public  responser<String> fendan(@PathVariable("year") String year,@RequestBody(required = false) bodypara ncc)
    {
        return  new responser<>(200,this.nesvouchsRepository.fendan(ncc.getNcc(),ncc.getPersoninfo(),year));
    }
    /**
     * 服务端下载查询结果

     */
    @PostMapping("downloadfile/{year}")
    public ResponseEntity downresult(@PathVariable("year") String yaear,@RequestBody download c)
    {
        List<newsvouchs> nesvouchs=this.nesvouchsRepository.getvouchde(c.ncc,yaear);
        String filename=c.ncc.getCpsn_num()+ DateUtil.getExcelDate(c.ncc.getEnddate())+".xlsx";
        try {
           return  toexcelbook(nesvouchs,c.columns);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
    /**
     * 导出到excel表格的逻辑
     * @param newsvouchs
     */
   private  ResponseEntity<byte[]> toexcelbook(List<newsvouchs> newsvouchs,List<String> datacolumns)
   {
       String filename="data"+UUID.randomUUID().toString()+".xlsx";
       try
       {

           File fileu=new File(filename);
           FileOutputStream fileOutputStream=
                   new FileOutputStream(fileu);
           Workbook workbook=this.excekbook.workbook(newsvouchs,datacolumns,newsvouchs.class);
           workbook.write(fileOutputStream);
           Path path= Paths.get(filename);
           Resource resource=new InputStreamResource(Files.newInputStream(path));
           byte[] file=resource.getInputStream().readAllBytes();
           HttpHeaders headers = new HttpHeaders();
           headers.add("Content-Disposition", "attachment;filename="+filename);
           headers.add("Content-Type", "application/octet-stream");
           headers.add("Pragma","no-cache");
           headers.add("Cache-Control","No-cache");
           var response=ResponseEntity.ok().header("Content-Disposition", "attachment;filename="+filename)
                   .body(file);
          if(fileu.delete())
          {
              System.out.println("删除成功");
          }
           return  response;
          // return  new ResponseEntity<byte[]>(file,headers,HttpStatus.OK);

       }
       catch (Exception ex)
       {
           responser rs=new responser();
           rs.setResult("文件下载错误");
           rs.setStatus(-1);
           return  null;



       }

   }

    /**
     * 打印凭证
     *
     * @param c
     * @param ufzhangtao
     * @return
     * @throws IOException
     */
   @PostMapping("printufvouch/{ufzhangtao}")
   public ResponseEntity<byte[]> glvouchpdfprint(@PathVariable("ufzhangtao")String ufzhangtao,@RequestBody() vouchcontioan c) throws IOException {
       ufdatabasebasic info=new ufdatabasebasic();
       //LocalDateTime df=LocalDateTime.from(c.getBegindate().toInstant().atZone(ZoneId.systemDefault()));
       info.setYear(c.getBegindate().getYear());
       info.setZhangtaohao(ufzhangtao);
       List<Gl_Accvouch> newsvouchs= glvouchs.dymamicvouchs(info,c);
       String filename="data"+UUID.randomUUID().toString()+".pdf";
      // File fileu=new File(filename);
      File document=this.pdfUtil.ufdpdfformvouchs(ufzhangtao,newsvouchs,6,filename);
       try{





       Path path=Paths.get(filename);
       Resource resource=new InputStreamResource(Files.newInputStream(path));
       //FileSystemResource fielr=new FileSystemResource(fileu);
       byte[] file=resource.getInputStream().readAllBytes();
       HttpHeaders headers = new HttpHeaders();
       headers.add("Content-Disposition", "attachment;filename="+filename);
       headers.add("Content-Type", "application/octet-stream");
       headers.add("Pragma","no-cache");
       headers.add("Cache-Control","No-cache");
       var response=ResponseEntity.ok().header("Content-Disposition", "attachment;filename="+filename)
               .body(file);
      if(document.delete())
       {
           System.out.println("删除成功");
       }
       return  response;
       }
       catch (Exception e)
       {
           System.out.println(e.getMessage());
           return  null;

       }


   }

    /**
     * 增加付款通单
     * @param cdigest
     * @param year
     * @param needdo
     * @return
     */
   @PostMapping("addbanknode/{year}")
    public  responser<String> addbannode(@Param("cdigest")String  cdigest,@PathVariable("year") String year,@RequestBody List<newsvouchs> needdo)
   {
       Integer v=this.nesvouchsRepository.addbannode(needdo,cdigest,year);
       if(v>0)
       {
           return  new responser<>(200,"数据保存成功");
       }
       else
       {
           return new responser<>(500,"系统错误");
       }

   }
}


