package com.sqds.fileutil.pdfutil;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.sqds.filelutil.excelutil.ExceLTitle;
import com.sqds.ufdataManager.model.ufdata.Department;
import com.sqds.ufdataManager.model.ufdata.Gl_Accvouch;
import com.sqds.ufdataManager.model.ufdata.code;
import com.sqds.ufdataManager.model.ufsystem.Ua_Account;
import com.sqds.ufdataManager.registory.DataDml.Gl_AccVouchRepositoryHelp;
import com.sqds.ufdataManager.registory.DataDml.ufdatabasebasic;
import com.sqds.ufdataManager.registory.ufdata.DepartmentRepository;
import com.sqds.ufdataManager.registory.ufdata.Gl_AccVouchRepository;
import com.sqds.ufdataManager.registory.ufdata.codeRepository;
import com.sqds.ufdataManager.registory.ufsystem.Ua_AccountRepository;
import com.sqds.ufdataManager.registory.ufsystem.Ua_periodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class PdfUtil {

    @Autowired
    codeRepository codeRepository;
    @Autowired
    ufdatabasebasic info;
    @Autowired
    Ua_periodRepository periodRepository;
    @Autowired
    Ua_AccountRepository accountRepository;
    @Autowired
    Gl_AccVouchRepository glaccvouchmanager;
    @Autowired
    DepartmentRepository departmentRepository;



    public static <T> Document getdocument(String name, Set<String> title, List<T> data) throws DocumentException, FileNotFoundException {
        Document result=new Document(PageSize.A4);
        ///得到类的标头
        PdfWriter writer=PdfWriter.getInstance(result,
                new FileOutputStream(new File("test.pdf")));
        PdfPTable resulttable=new PdfPTable(title.size());
        resulttable.setWidthPercentage(100);
        resulttable.setSpacingBefore(10);
        title.forEach(t->{
            PdfPCell cell=new PdfPCell(new Paragraph(t));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            resulttable.addCell(cell);
        });

        if(!data.isEmpty())
         {
             //选择要生成pdf的文件
             Map<String,String> fields=new HashMap<>();
             Arrays.stream(data.get(0).getClass().getDeclaredFields())
                             .forEach(s->{
                                 ExceLTitle exceLTitle=
                                         s.getAnnotation(ExceLTitle.class);
                                 if(title.contains(exceLTitle.title()))
                                 {
                                     fields.put(s.getName(),exceLTitle.title());
                                 }

                             });

              data.stream().forEach(s->{

                Field[] f= s.getClass().getDeclaredFields();
                for(int i=0;i<title.size();i++)
                {
                    Optional<Field> fc= Arrays.stream(f).filter(cc->{
                        ExceLTitle title1=cc.getAnnotation(ExceLTitle.class);
                        return  title1.title().equals(title1.title());
                    }).findFirst();
                    if(fc.isPresent())
                    {
                        Field vt=fc.get();
                        vt.setAccessible(true);
                        try {

                            PdfPCell cell=new PdfPCell(new Paragraph(vt.get(s).toString()));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            resulttable.addCell(cell);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }



                }


              });
             
         }

       result.add(resulttable);
       result.close();
       writer.close();
        return  result;
    }

    public File ufdpdfformvouchs(String zhangtaohao, List<Gl_Accvouch> glAccvouches, int maxcount, String file)
    {
        Document document = new Document();
        try {




            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            Rectangle ufvouch=new Rectangle(570f,340f);
            document.setPageSize(ufvouch);
            //document.newPage();
            document.setMargins(40, 10, 10, 10);
            Ua_Account uaAccount=this.accountRepository.finFirstaccount(zhangtaohao);
            int pagesize=glAccvouches.size()%maxcount;
            if(pagesize>0)
            {
                for (int p=0;p<maxcount-pagesize;p++)
                {
                    Gl_Accvouch blackvouchs=new Gl_Accvouch();
                    glAccvouches.add(blackvouchs);
                }
            }
            int firstpage=0;
            ///按照没6条分一页
            BigDecimal[] sum={BigDecimal.valueOf(0),BigDecimal.valueOf(0)};
            for(int i=0;i<glAccvouches.size();i+=maxcount)
            {
                List<Gl_Accvouch> partvouchs=glAccvouches.subList(i,maxcount+i);
                partvouchs.forEach(s->{
                    if(s.getMd()!=null)
                    {
                        sum[0]=sum[0].add(s.getMd());
                    }
                    if(s.getMc()!=null)
                    {
                        sum[1]=sum[1].add(s.getMc());
                    }


                });
                greatprint(document,uaAccount,partvouchs,firstpage/maxcount+1,glAccvouches.size()/maxcount,sum);
                firstpage+=maxcount;
            }
            document.close();

            return  new File(file);
        }
        catch (Exception e)
        {
            return  null;

        }
        finally {
            document.close();
        }


    }
    public Document ufpdfdocument(String zhangtaohao, List<Gl_Accvouch> glAccvouches, int maxcount, File file)
    {
        try {

            Document document = new Document();


            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            Rectangle ufvouch=new Rectangle(570f,340f);
            document.setPageSize(ufvouch);
            //document.newPage();
            document.setMargins(40, 10, 10, 10);
            Ua_Account uaAccount=this.accountRepository.finFirstaccount(zhangtaohao);
            int pagesize=glAccvouches.size()%maxcount;
            if(pagesize>0)
            {
                for (int p=0;p<maxcount-pagesize;p++)
                {
                    Gl_Accvouch blackvouchs=new Gl_Accvouch();
                    glAccvouches.add(blackvouchs);
                }
            }
            int firstpage=0;
            ///按照没6条分一页
            BigDecimal[] sum={BigDecimal.valueOf(0),BigDecimal.valueOf(0)};
            for(int i=0;i<glAccvouches.size();i+=maxcount)
            {
                List<Gl_Accvouch> partvouchs=glAccvouches.subList(i,maxcount+i);
                partvouchs.forEach(s->{
                    if(s.getMd()!=null)
                    {
                        sum[0]=sum[0].add(s.getMd());
                    }
                     if(s.getMc()!=null)
                    {
                        sum[1]=sum[1].add(s.getMc());
                    }


                });
                greatprint(document,uaAccount,partvouchs,firstpage/maxcount+1,glAccvouches.size()/maxcount,sum);
                firstpage+=maxcount;
            }
            document.close();

            return  document;
        }
        catch (Exception e)
        {
            System.out.println("printex"+e.getMessage());
            return  null;

        }

    }


    /**
     * 生产pdf打印文件
     * @param zhangtaohao
     * @param glAccvouches
     * @param maxpagesize
     * @return
     */
    public int greatprint(Document document, Ua_Account zhangtaohao, List<Gl_Accvouch> glAccvouches, int pagenum, int maxpagesize, BigDecimal[] sum) {
        try {
            //if(pagenum>1)
            {
                document.newPage();
            }
            ResourceLoader resourceLoader = new FileSystemResourceLoader();
            Resource fileresource = resourceLoader.getResource("simsun.ttc");

            BaseFont bt = BaseFont.createFont(Objects.requireNonNull(fileresource.getFile().getPath()) + ",0", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            Font chinesefount = new Font(bt, 20f, Font.BOLD, BaseColor.BLACK);

            Paragraph caption = new Paragraph("记账凭证", chinesefount);
            caption.setAlignment(Element.ALIGN_CENTER);
            //caption.setFont(chinesefount);
            document.add(caption);
            caption = new Paragraph("=============");
            caption.setAlignment(Element.ALIGN_CENTER);
            document.add(caption);
            Paragraph unitriqi = new Paragraph();
            chinesefount.setSize(11f);
            Chunk dangwei = new Chunk("编制单位:"+zhangtaohao.getCAcc_Name(), chinesefount);
            unitriqi.add(dangwei);
            unitriqi.add("                 ");
            DateFormat chinesedate = new SimpleDateFormat("yyyy年MM月dd日");
            String date = chinesedate.format(glAccvouches.get(0).getDbill_date());
            chinesefount.setSize(11f);
            chinesefount.setStyle(Font.NORMAL);
            Chunk datepara = new Chunk(date, chinesefount);
            datepara.setFont(chinesefount);
            unitriqi.add(datepara);
            document.add(unitriqi);
            Paragraph bianzhidanwei = new Paragraph();
            Chunk accountunit = new Chunk("核算单位:["+zhangtaohao.getCAcc_Id()+"]"+zhangtaohao.getCAcc_Name(), chinesefount);
            bianzhidanwei.add(accountunit);
            String code = "第"+glAccvouches.get(0).getIno_id()+"号-"+this.getino_id(pagenum,maxpagesize);
            String black = "";
            Rectangle rectangle = document.getPageSize();
            Float back = rectangle.getWidth()- accountunit.getWidthPoint()- code.length() - 20;
            for (int si = 0; si <=60; si++) {
                black += "\u00A0";
            }
            bianzhidanwei.add(black);
            Chunk vouchcdoe = new Chunk(code, chinesefount);
            bianzhidanwei.add(vouchcdoe);
            document.add(bianzhidanwei);
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setTotalWidth(new float[]{100, 160, 100, 100});
            table.setHorizontalAlignment(Element.ALIGN_CENTER);
            chinesefount.setSize(10f);


            //table.addCell("1");
            //table.addCell("1");
           // table.addCell("1");
            String[] tite = {"摘要", "会计科目", "借方", "贷方"};
            NumberFormat format=new DecimalFormat("#.00");
             for(String s:tite)
             {
                 PdfPCell cell = new PdfPCell(new Paragraph(s, chinesefount));
                 cell.setFixedHeight(20);
                 table.addCell(cell);
             }
            for (Gl_Accvouch vouchs : glAccvouches) {
                PdfPCell contentcell = new PdfPCell(new Paragraph(vouchs.getCdigest(), chinesefount));
                contentcell.setFixedHeight(25);
                table.addCell(contentcell);
                String codename=vouchs.getCcode()==null?"":getchinesecodefromgl(zhangtaohao.getCAcc_Id(),vouchs);
                contentcell = new PdfPCell(new Paragraph(codename, chinesefount));
                table.addCell(contentcell);

                contentcell = new PdfPCell(new Paragraph(vouchs.getMd()==null || vouchs.getMd().intValue()==0 ?"":format.format(vouchs.getMd())));
                table.addCell(contentcell);
                contentcell = new PdfPCell(new Paragraph(vouchs.getMc()==null || vouchs.getMc().intValue()==0?"":format.format(vouchs.getMc())));
                table.addCell(contentcell);


            }
        PdfPCell footcell = new PdfPCell(new Paragraph("附单据数 3张", chinesefount));
          table.addCell(footcell);
           footcell = new PdfPCell(new Paragraph("合计"+(pagenum<maxpagesize?"":getchinesrnum(sum[0].doubleValue())), chinesefount));
           table.addCell(footcell);
          footcell = new PdfPCell(Phrase.getInstance(format.format(sum[0])));
           table.addCell(footcell);
           footcell = new PdfPCell(Phrase.getInstance(format.format(sum[1])));
            table.addCell(footcell);


            document.add(table);
            String[] footc = {"财务主管", "记账", "复核", "出纳", "制单", "经办人"};
            Paragraph footpar = new Paragraph();
            Arrays.stream(footc).forEach(s -> {
                Chunk fc = new Chunk(s, chinesefount);
                footpar.add(fc);
                String blackfc = "";
                if(s.equals("制单"))
                {
                    System.out.println(glAccvouches.get(0).getCbill());
                    footpar.add(new Chunk(glAccvouches.get(0).getCbill(),chinesefount));
                }
                else {
                    for (int sr = 0; sr < 15; sr++) {

                        blackfc=blackfc+"\u00A0";

                    }

                footpar.add(blackfc);
                }
            });

            document.add(footpar);



        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
        return 0;
    }

    /**
     * 从凭证得到科目代码的名字
     * @param zhangtaohao
     * @param vouchs
     * @return
     */
    public  String getchinesecodefromgl(String zhangtaohao, Gl_Accvouch vouchs)
    {
        int year=this.periodRepository.findMaxPeriodByCAcc_id(zhangtaohao);
        this.info.setYear(year);
        this.info.setZhangtaohao(zhangtaohao);
        int mincode=vouchs.getCcode().length()%2==0?4:3;
        List<code> codes= this.codeRepository.findAllByCcode(vouchs.getCcode().substring(0,mincode),info);
        codes.sort((s,t)->s.getIgrade()-t.getIgrade());
        AtomicReference<String> resultcode= new AtomicReference<>("");
        for(var i=vouchs.getCcode().length();i>=mincode;i-=2)
        {
            int finalI = i;
            String subcode=vouchs.getCcode().substring(0, finalI);
            code mcode=codes.stream().filter(s->s.getCcode().equals(vouchs.getCcode().substring(0, finalI))).findFirst().get();
            resultcode.set(mcode.getCcode_name()+resultcode);
            if(i!=mincode)resultcode.set("/"+resultcode);
        }

//        codes.forEach(s->{
//            resultcode.set(resultcode + s.getCcode_name());
//            if(codes.indexOf(s)<codes.size()-1)
//            {
//                resultcode.set(resultcode+"/");
//            }
//
//
//        });
        code org=codes.stream().filter(s->s.getCcode().equals(vouchs.getCcode())).findFirst().get();
        boolean isax=org.getBcus() || org.getBdept() || org.getBitem() ||org.getBsup() ||org.getBperson();
        //如果有辅助项目查找辅助项目的核算
        if(isax) {
            Map<String, List<Gl_AccVouchRepositoryHelp.axinfo>> codeinfo = glaccvouchmanager.getaxinfofromCode(info, vouchs.getCcode());
            String key = "";
            if (!codeinfo.isEmpty()) {
                key = org.getBcus() ? "cus" : "";
                key = org.getBdept() ? "dep" : "";
                key = org.getBperson() ? "person" : "";
                key = org.getBsup() ? "sup" : "";
                key = org.getBitem() ? "item" : "";
                Gl_AccVouchRepositoryHelp.axinfo ax = new Gl_AccVouchRepositoryHelp.axinfo();
                ax.setTitle("");
                switch (key) {
                    case "dep":
                        ax = codeinfo.get(key).stream().filter(s -> s.getValue().equals(vouchs.getCdept_id())).findFirst().get();
                        break;
                    case "person":
                        ax = codeinfo.get(key).stream().filter(s -> s.getValue().equals(vouchs.getCperson_id())).findFirst().get();
                        break;
                    case "cus":
                        ax = codeinfo.get(key).stream().filter(s -> s.getValue().equals(vouchs.getCcus_id())).findFirst().get();
                        break;
                    case "sup":
                        ax = codeinfo.get(key).stream().filter(s -> s.getValue().equals(vouchs.getCsup_id())).findFirst().get();
                        break;
                    case "item":
                        ax = codeinfo.get(key).stream().filter(s -> s.getValue().equals(vouchs.getCitem_id())).findFirst().get();
                        break;
                    default:
                        break;
                }
                resultcode.set(resultcode + ax.getTitle());

            }
        }




        return  resultcode.get();
    }

    public Gl_Accvouch getaxinfo(String zhangtaohao,Gl_Accvouch vouchs)
    {
        int year=this.periodRepository.findMaxPeriodByCAcc_id(zhangtaohao);
        this.info.setYear(year);
        this.info.setZhangtaohao(zhangtaohao);
        int mincode=vouchs.getCcode().length()%2==0?4:3;
        List<code> codes= this.codeRepository.findAllByCcode(vouchs.getCcode().substring(0,mincode),info);
        codes.sort((s,t)->s.getIgrade()-t.getIgrade());
        AtomicReference<String> resultcode= new AtomicReference<>("");
        for(var i=vouchs.getCcode().length();i>=mincode;i-=2) {
            int finalI = i;
            String subcode = vouchs.getCcode().substring(0, finalI);
            code mcode = codes.stream().filter(s -> s.getCcode().equals(vouchs.getCcode().substring(0, finalI))).findFirst().get();
            resultcode.set(mcode.getCcode_name() + resultcode);
            if (i != mincode) resultcode.set("/" + resultcode);
        }
        code org=codes.stream().filter(s->s.getCcode().equals(vouchs.getCcode())).findFirst().get();
        boolean isax=org.getBcus() || org.getBdept() || org.getBitem() ||org.getBsup() ||org.getBperson();
        //如果有辅助项目查找辅助项目的核算
        if(isax) {
            Map<String, List<Gl_AccVouchRepositoryHelp.axinfo>> codeinfo = glaccvouchmanager.getaxinfofromCode(info, vouchs.getCcode());
            String key = "";
            if (!codeinfo.isEmpty()) {
                key = org.getBcus() ? "cus" : key;
                key = org.getBdept() ? "dep" : key;
                key = org.getBperson() ? "person" : key;
                key = org.getBsup() ? "sup" : key;
                key = org.getBitem() ? "item" : key;
                Gl_AccVouchRepositoryHelp.axinfo ax = new Gl_AccVouchRepositoryHelp.axinfo();
                ax.setTitle("");
                switch (key) {
                    case "dep":
                        ax = codeinfo.get(key).stream().filter(s -> s.getValue().equals(vouchs.getCdept_id())).findFirst().get();
                        vouchs.setCdept_id(ax.getTitle());
                        break;
                    case "person":
                        ax = codeinfo.get(key).stream().filter(s -> s.getValue().equals(vouchs.getCperson_id())).findFirst().get();
                        Department department=this.departmentRepository.getpartment(vouchs.getCdept_id(),info).get(0);

                        vouchs.setCdept_id(department.getCDepName());;
                        vouchs.setCperson_id(ax.getTitle());

                        break;
                    case "cus":
                        ax = codeinfo.get(key).stream().filter(s -> s.getValue().equals(vouchs.getCcus_id())).findFirst().get();
                        vouchs.setCcus_id(ax.getTitle());
                        break;
                    case "sup":
                        ax = codeinfo.get(key).stream().filter(s -> s.getValue().equals(vouchs.getCsup_id())).findFirst().get();
                        vouchs.setCsup_id(ax.getTitle());
                        break;
                    case "item":
                        ax = codeinfo.get(key).stream().filter(s -> s.getValue().equals(vouchs.getCitem_id())).findFirst().get();
                        vouchs.setCitem_id(ax.getTitle());
                        break;
                    default:
                        break;
                }
                resultcode.set(resultcode + ax.getTitle());

            }
        }

       vouchs.setCcode(resultcode.get());


        return  vouchs;

    }

    String getino_id(Integer ino_id,Integer maxino_id)
    {
        String code=maxino_id.toString();
        String incode=ino_id.toString();
        String panter="0000";
        code=panter.substring(0,panter.length()-code.length())+code;
        incode=panter.substring(0,panter.length()-incode.length())+incode;
//        for(int i=0;i<4-maxino_id/10;i++)
//        {
//            code="0"+code;
//            incode="0"+incode;
//
//        }
        return  incode+"/"+code;


    }
    public String  getchinesrnum(Double needchangenum)
    {
        String[] chinese={"壹","贰","叁","肆","伍","陆","柒","捌","玖"};
        String[] unit={"元","拾","佰","仟","万","亿"};
        int numpart=needchangenum.intValue();
        DecimalFormat decimalFormat=new DecimalFormat("#.00");

        Double divm=Double.valueOf(decimalFormat.format(needchangenum-numpart))*100;
        int decpart=divm.intValue();
        System.out.println(divm);
        String chinesenum="";

        for(int i=0;i<unit.length-3 && numpart>0;i++)
        {

            System.out.println(numpart%10000);
            int part=numpart%10000;

            switch (i) {
                case 0:
                    unit[0]="元";
                    break;
                case 1:
                    unit[0]="万";
                    break;
                case 2:
                    unit[0]="亿";
                    break;

                default:
                    break;
            }

            int unitnum=0;
            if(part>0)
            {
                while (part/10>0)
                {
                    String cin=part%10==0?"零":chinese[part%10-1];
                    if(part%10>0)
                    {
                        chinesenum=chinese[part%10-1]+unit[unitnum]+chinesenum;
                    }
                    else
                    {
                        if((part/10)%10>0) {
                            chinesenum = (unitnum == 0 || chinesenum.equals("元") || i==0? unit[0] : "零") + chinesenum;
                        }
                    }

                    unitnum++;
                    part/=10;
                    System.out.println(unitnum);

                }
                chinesenum=chinese[part%10-1]+unit[unitnum]+chinesenum;
                System.out.println(chinesenum);

                if(numpart/10000!=0 && unitnum<3 )
                {
                    chinesenum="零"+chinesenum;
                }
            }
            else
            {
                chinesenum=unit[0]+chinesenum;
            }
            System.out.println(unitnum);


            numpart/=10000;

            System.out.println(numpart);

        }
        String[] divpartunit={"分","角"};
        int divnum=0;
        String xiaoshu="";
        if(decpart>0) {
			/*if(decpart/10 == 0) {
				divnum++;
				xiaoshu = "零" + xiaoshu;

			}*/
            while (decpart % 10 > 0) {
                int divs = decpart % 10;
                System.out.println(divs);
                xiaoshu = chinese[divs - 1] + divpartunit[divnum] + xiaoshu;
                decpart /= 10;
                divnum++;


            }
        }
        if(decpart/10==0 && decpart>0)xiaoshu="零"+xiaoshu;
        chinesenum=chinesenum+xiaoshu;
        System.out.println(chinesenum);
        return  chinesenum;

    }


}
