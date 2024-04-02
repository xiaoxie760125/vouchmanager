package com.sqds.filelutil.excelutil;


import com.sqds.springjwtstudy.hentity.responser;
import com.sqds.ufdataManager.model.ufdata.person;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component

public class   excekbook<T>  {





    public  excekbook()
  {


  }
    public  Workbook  workbook(List<T> data,Field[] filelds) throws IllegalAccessException, IOException {
        //FileOutputStream outputStream=new FileOutputStream("workdemo1.xlsx");
        //FileInputStream inputStream=new FileInputStream(demofile);
        Workbook workbook = new XSSFWorkbook();
        try {




            Sheet sheet=workbook.createSheet("data");
            Row row=sheet.createRow(0);
            CellStyle style=workbook.createCellStyle();
            style.setBorderBottom(CellStyle.BORDER_DOTTED);
            style.setBorderLeft(CellStyle.BORDER_THIN);
            style.setBorderRight(CellStyle.BORDER_THIN);
            style.setBorderTop(CellStyle.BORDER_THIN);
            style.setAlignment(CellStyle.ALIGN_CENTER);
            style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

            Font font=workbook.createFont();
            font.setFontName("Arial");

            font.setFontHeightInPoints((short)14);
            style.setFont(font);

            if(row==null)
            {

                row=sheet.createRow(0);
                // row.setRowStyle(style);
            }

            for(int i=0;i<filelds.length;i++)
            {
                Field filed=filelds[i];
                //对存在标头的字段导入
                ExceLTitle title=filed.getAnnotation(ExceLTitle.class);
                if(title!=null) {


                    Cell cell = row.createCell(i);
                    style.setBorderTop(CellStyle.BORDER_MEDIUM);
                    if (i == 0) {
                        style.setBorderLeft(CellStyle.BORDER_MEDIUM);


                    }
                    if (i == filelds.length - 1) {
                        style.setBorderRight(CellStyle.BORDER_MEDIUM);
                    } else {
                        style.setBorderLeft(CellStyle.BORDER_THIN);
                        style.setBorderRight(CellStyle.BORDER_THIN);
                    }
                    cell.setCellStyle(style);
                    ;
                    title = filed.getAnnotation(ExceLTitle.class);
                    sheet.setColumnWidth(i, 15 * 256);
                    if (title != null) {
                        cell.setCellValue(title.column().isEmpty()?title.title():title.column());
                    } else {
                        cell.setCellValue(filed.getName());
                    }
                }
            }
            for(int i=0;i<data.size();i++) {
                Row row1 = sheet.createRow(i + 1);

                T t =data.get(i);
                if(i==data.size()-1)
                {
                    style.setBorderBottom(CellStyle.BORDER_MEDIUM);
                }

                for (int j = 0; j < filelds.length; j++) {
                    ExceLTitle nt=filelds[j].getAnnotation(ExceLTitle.class);
                    style.setBorderTop(CellStyle.BORDER_THIN);
                    if(j==0) style.setBorderLeft(CellStyle.BORDER_MEDIUM);
                    else if (j==filelds.length-1) {
                        style.setBorderRight(CellStyle.BORDER_MEDIUM);

                    }
                    else
                    {
                        style.setBorderRight(CellStyle.BORDER_THIN);
                        style.setBorderRight(CellStyle.BORDER_THIN);
                    }

                    Field filed = filelds[j];
                    Cell c = row1.createCell(j);
                    c.setCellStyle(style);
                    filed.setAccessible(true);
                    switch (filed.getType().getSimpleName()) {
                        case "String":
                            c.setCellValue(filed.get(t).toString());
                            break;
                        case "Integer":
                        case "int":

                            c.setCellValue((filed.get(t)==null?0:Integer.parseInt(filed.get(t).toString())));
                            break;
                        case "Double":
                        case  "double":
                            c.setCellValue(filed.get(t)==null? (double) 0 :filed.getDouble(t));
                            break;
                        case "person":
                            person ufperson=(person)filed.get(t);
                            if(ufperson!=null) {
                                c.setCellValue(ufperson.getCPsn_Name());
                            }
                            else
                            {
                                c.setCellValue("");
                            }
                        break;
                        case "Float":
                            c.setCellValue(filed.get(t)==null?(float)0:filed.getFloat(t));
                            break;
                        case "BigDecimal":
                            if(filed.get(t)!=null)
                            {
                             BigDecimal dc=(BigDecimal)filed.get(t);
                             if(nt.isperenage() && dc.floatValue()<=1 && dc.floatValue()>0)
                             {
                                 NumberFormat numberFormat=NumberFormat.getInstance();
                                 numberFormat.setMinimumIntegerDigits(2);
                                 c.setCellValue(numberFormat.format(dc.multiply(BigDecimal.valueOf(100)).floatValue())+"%");

                             }
                             else
                             {
                                 c.setCellValue(dc.floatValue());
                             }}
                            else
                            {
                                c.setCellValue(0);
                            }


                            break;
                        case "Date":
                        case "LocalDate":
                        case "LocalDateTime":
                            Date date=(Date) filed.get(t);
                             DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
                             dateFormat.format(date);


                                c.setCellValue(dateFormat.format(date));


                            break;
                        case "boolean":
                        case "Boolean":
                            c.setCellValue((boolean)(filed.get(t))?"是":"否");
                            break;

                        default:
                            c.setCellValue((filed.get(t)).toString());
                            break;

                    }

                }

            }
          /*
          基本格式化表格
           */

        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());

        }





        // workbook.write(outputStream);

        return workbook;
    }

    public  Workbook  workbook(List<T> data) throws IllegalAccessException, IOException {
        //FileOutputStream outputStream=new FileOutputStream("workdemo1.xlsx");
        //FileInputStream inputStream=new FileInputStream(demofile);
        Workbook workbook = new HSSFWorkbook();
      try {

          Class clazz =((T)data.get(0)).getClass();
          Field[] filelds= Arrays.stream(clazz.getDeclaredFields()).filter(s->{
              ExceLTitle vt=s.getAnnotation(ExceLTitle.class);
              return  vt!=null;
          }).toArray(Field[]::new);
          return  workbook(data,filelds);

          /*
          基本格式化表格
           */

      }
      catch (Exception e)
      {
          System.out.println(e.getMessage());

      }





   // workbook.write(outputStream);

        return workbook;
    }

       public Workbook workbook(List<T> data,List<String> columns,Class<T> dataclass) throws IllegalAccessException, IOException {

           try {
                Workbook workbook=new HSSFWorkbook();
                //划重点，反射类的获得
          

               final int[] index = {0};

               Field[]  orgfile=dataclass.getDeclaredFields();


               Field[] datafiels= Arrays.stream(dataclass.getDeclaredFields()).filter(s->{

                  ExceLTitle exceLTitle=s.getAnnotation(ExceLTitle.class);
                  return  exceLTitle!=null && columns.contains(exceLTitle.title());
               }).sorted((o1, o2)->{
                   Integer fistindox=columns.indexOf(o1.getAnnotation(ExceLTitle.class).title());
                   Integer secondindox=columns.indexOf(o2.getAnnotation(ExceLTitle.class).title());
                   return fistindox-secondindox;
               }).toArray(Field[]::new);



                return  workbook(data,datafiels);

           }
           catch (Exception e)
           {
                return null;
           }



   }
    public   ResponseEntity<byte[]> toexcelbook(List<T> newsvouchs, List<String> datacolumns,Class<T> modelclass)
    {
        String filename="data"+UUID.randomUUID().toString()+".xlsx";
        try
        {

            File fileu=new File(filename);
            FileOutputStream fileOutputStream=
                    new FileOutputStream(fileu);

            Workbook workbook=this.workbook(newsvouchs,datacolumns,modelclass);
            workbook.write(fileOutputStream);
            Path path= Paths.get(filename);
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
 //从excel前端文件上传
   public List<T> getDatalistfromweb(MultipartFile file,Class<T> c) throws InstantiationException, IllegalAccessException, IOException {
  
    String filename=file.getOriginalFilename();
    String prefix=filename.substring(filename.lastIndexOf("."));
    Path tempexcelfile=Files.createTempFile(UUID.randomUUID().toString(),prefix);
    return this.getDataList(tempexcelfile.toFile(), c);
  
   }


    /**
     * excel表導入數據
     * @param files
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public List<T> getDataList(File files,Class<T> c) throws InstantiationException, IllegalAccessException {
           

       
        //以文件标头导入是需要对应相应的字段名称
        Field[] fields= Arrays.stream(c.getDeclaredFields())
                .filter(f->f.isAnnotationPresent(ExceLTitle.class))
                .toArray(Field[]::new);
        List<T> list=new ArrayList<>();

        try {
            Workbook workbook=WorkbookFactory.create(files);
            Sheet sheet=workbook.getSheetAt(0);
            Row row=sheet.getRow(0);
            int maxcomns=row.getLastCellNum();
            int max=sheet.getLastRowNum();
            for(int rowindex=1;rowindex<=sheet.getLastRowNum();rowindex++) {

                T value =c.newInstance();
                Row rowcount = sheet.getRow(rowindex);
                 

                if(rowcount.getCell(0).getCellType()==Cell.CELL_TYPE_BLANK)
                {
                    break;
                }
               
                int finalI=0;

                for (int i = 0; i < maxcomns; i++) {
                    if(finalI>maxcomns-1)break;

                    String title = row.getCell(finalI).getStringCellValue();

                    Optional<Field> sr= Arrays.stream(fields).filter(r->r.getAnnotation(ExceLTitle.class).title().equals(title)).findFirst();
                    if (sr.isPresent()) {
                       System.out.println(title+":"+rowcount.getCell(finalI).getCellType());
                        Field s=sr.get();
                            s.setAccessible(true);
                            switch (s.getType().getSimpleName()) {
                                case "String":
                                    if( rowcount.getCell(finalI).getCellType()==Cell.CELL_TYPE_NUMERIC || rowcount.getCell(finalI).getCellType()==Cell.CELL_TYPE_ERROR)
                                    {
                                        s.set(value,String.valueOf(Double.valueOf(rowcount.getCell(finalI).getNumericCellValue()).intValue()));
                                    }
                                    else
                                    {

                                        s.set(value,rowcount.getCell(finalI).getStringCellValue());
                                    }
                                    break;
                                case "Integer":
                                    case "int":
                                        if(rowcount.getCell(finalI).getCellType()==Cell.CELL_TYPE_NUMERIC) {
                                            s.set(value,
                                                    (Double.valueOf(rowcount.getCell(finalI).getNumericCellValue())).intValue());
                                        }
                                        else
                                        {
                                            s.set(value,Integer.parseInt(rowcount.getCell(finalI).getStringCellValue()));
                                        }
                                    break;
                                case "float":
                                case "Float":
                                    if(rowcount.getCell(finalI).getCellType()==Cell.CELL_TYPE_NUMERIC || rowcount.getCell(finalI).getCellType()==Cell.CELL_TYPE_FORMULA) {
                                        s.set(value, (float)rowcount.getCell(finalI).getNumericCellValue());
                                    }
                                    else
                                    {
                                        s.set(value,Float.parseFloat(convertoperson(rowcount.getCell(finalI).getStringCellValue())));
                                    }
                                    break;
                                case "Double":
                                case  "double":
                                    if(rowcount.getCell(finalI).getCellType()==Cell.CELL_TYPE_NUMERIC || rowcount.getCell(finalI).getCellType()==Cell.CELL_TYPE_FORMULA) {
                                        s.set(value, rowcount.getCell(finalI).getNumericCellValue());
                                    }
                                    else
                                    {
                                        s.set(value,Double.parseDouble(rowcount.getCell(finalI).getStringCellValue()));
                                    }
                                    break;
                                case "BigDecimal":
                                    if(rowcount.getCell(finalI).getCellType()==Cell.CELL_TYPE_STRING)
                                    {
                                        s.set(value,new BigDecimal(rowcount.getCell(finalI).getStringCellValue()));
                                    }
                                    else {
                                        s.set(value, BigDecimal.valueOf(rowcount.getCell(finalI).getNumericCellValue()));
                                    }
                                     break;
                                    case "Boolean":
                                    Boolean va = rowcount.getCell(finalI).getStringCellValue() == "是" ? true : false;
                                    s.set(value, va);
                                    break;
                           /*     case "LocalDate":
                                    LocalDate date=LocalDate.parse(rowcount.getCell(finalI).getStringCellValue());
                                    s.set(value,date);
                                    break;
                                case "LocalDateTime":

                                    LocalDateTime datim=LocalDateTime.parse(rowcount.getCell(finalI).getStringCellValue());
                                    s.set(value,datim);
                                    break;*/
                                case "Date":
                                case "LocalDate":
                                case "LocalDateTime":
                                    Date date1=new Date();
                                    if(rowcount.getCell(finalI).getCellType()==Cell.CELL_TYPE_STRING || rowcount.getCell(finalI).getCellType()==Cell.CELL_TYPE_FORMULA)
                                    {
                                        String datevalue=rowcount.getCell(finalI).getStringCellValue();
                                        String pattern="(?<y>\\d{4})-(?<m>\\d{2})-(?<d>\\d{2})\\s*(?<h>\\d{2}:\\d{2}:\\d{2})?";
                                        Pattern datepater=Pattern.compile(pattern);
                                        Matcher matcher=datepater.matcher(rowcount.getCell(finalI).getStringCellValue());
                                        if(matcher.find())
                                        {
                                            date1=DateUtil.parseYYYYMMDDDate(matcher.group("y")+"/"+matcher.group("m")+"/"+matcher.group("d"));
                                        }

                                        // date1=DateUtil.getJavaDate(rowcount.getCell(finalI).getNumericCellValue());
                                    }
                                    else {

                                      date1 = rowcount.getCell(finalI).getDateCellValue();
                                    }
                                    String type=s.getType().getSimpleName();
                                    if(type.equals("Date"))
                                    {
                                        s.set(value,date1);
                                    }
                                    else if(type.equals("LocalDateTime"))
                                    {
                                        s.set(value,date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                                    }
                                    else
                                    {
                                        s.set(value,date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                                    }




                                    default:

                                    break;


                        }



                    }
                    finalI=finalI+1;
                }
                list.add(value);
            }
          return list;



        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        catch (Exception e)
        {
            throw  new RuntimeException(e);
        }

        /*finally {
            return  list;
        }*/



    }
    private  String convertoperson(String numberformat)
    {
        if(numberformat.lastIndexOf("%")==numberformat.length()-1 && !numberformat.isEmpty())
        {
            return  Double.parseDouble(numberformat.substring(0,numberformat.length()-1))/100+"";
        }
        else if(numberformat.isEmpty())
        {
            return  "1";
        }
        else
        {
            return  numberformat;
        }
    }
    public  List<T> getDataList(FileInputStream files,Class<T> clazz) throws InstantiationException, IllegalAccessException {

    
        //以文件标头导入是需要对应相应的字段名称
        Field[] fields= Arrays.stream(clazz.getDeclaredFields())
                .filter(f->f.isAnnotationPresent(ExceLTitle.class))
                .toArray(Field[]::new);
        List<T> list=new ArrayList<>();
        try {
            Workbook workbook=new XSSFWorkbook(files);
            Sheet sheet=workbook.getSheetAt(0);
            Row row=sheet.getRow(0);
            int max=sheet.getLastRowNum();
            for(int rowindex=1;rowindex<=sheet.getLastRowNum();rowindex++) {
                T value = clazz.newInstance();
                Row rowcount = sheet.getRow(rowindex);
                for (int i = 0; i < fields.length; i++) {
                    String title = row.getCell(i).getStringCellValue();
                    int finalI = i;
                    Optional<Field> sr= Arrays.stream(fields).filter(r->r.getAnnotation(ExceLTitle.class).title().equals(title)).findFirst();
                    if (sr.isPresent()) {
                        Field s=sr.get();
                        s.setAccessible(true);
                        switch (s.getType().getSimpleName()) {
                            case "String":
                                if( rowcount.getCell(finalI).getCellType()==Cell.CELL_TYPE_NUMERIC)
                                {
                                    s.set(value,String.valueOf(Double.valueOf(rowcount.getCell(finalI).getNumericCellValue()).intValue()));
                                }
                                else
                                {
                                    s.set(value,rowcount.getCell(finalI).getStringCellValue());
                                }
                                break;
                            case "Integer":
                            case "int":
                                s.set(value,
                                        (Double.valueOf(rowcount.getCell(finalI).getNumericCellValue())).intValue());
                                break;
                            case "Double":
                            case  "double":
                                s.set(value, rowcount.getCell(finalI).getNumericCellValue());
                                break;
                            case "BigDecimal":
                                s.set(value,BigDecimal.valueOf(rowcount.getCell(finalI).getNumericCellValue()));
                                break;
                            case "Boolean":
                                Boolean va = rowcount.getCell(finalI).getStringCellValue() == "是" ? true : false;
                                s.set(value, va);
                                break;
                            case "LocalDate":
                                LocalDate date=LocalDate.parse(rowcount.getCell(finalI).getStringCellValue());
                                s.set(value,date);
                                break;
                            case "LocalDateTime":
                                LocalDateTime datim=LocalDateTime.parse(rowcount.getCell(finalI).getStringCellValue());
                                s.set(value,datim);
                                break;
                            case "Date":
                                Date date1=new Date();
                                if(rowcount.getCell(finalI).getCellType()==Cell.CELL_TYPE_STRING)
                                {
                                    String datevalue=rowcount.getCell(finalI).getStringCellValue();
                                    String pattern="(?<y>\\d{4})-(?<m>\\d{2})-(?<d>\\d{2})";
                                    Pattern datepater=Pattern.compile(pattern);
                                    Matcher matcher=datepater.matcher(rowcount.getCell(finalI).getStringCellValue());
                                    if(matcher.find())
                                    {
                                        date1=DateUtil.parseYYYYMMDDDate(matcher.group("y")+"/"+matcher.group("m")+"/"+matcher.group("d"));
                                    }
                                    // date1=DateUtil.getJavaDate(rowcount.getCell(finalI).getNumericCellValue());
                                }
                                else {
                                    date1 = rowcount.getCell(finalI).getDateCellValue();
                                }
                                s.set(value,date1);
                            default:
                                break;


                        }



                    }
                }
                list.add(value);
            }
            return list;



        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        catch (Exception e)
        {
            throw  new RuntimeException(e);
        }

        /*finally {
            return  list;
        }*/



    }



}
