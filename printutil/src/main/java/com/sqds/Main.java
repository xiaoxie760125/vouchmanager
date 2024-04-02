package com.sqds;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Document document=new Document();
        try {
            ResourceLoader resourceLoader=new FileSystemResourceLoader();
            Resource fileresource=resourceLoader.getResource("classpath:ziti/simsun.ttc");
            if(fileresource.exists())
            {
                System.out.println("fileexit");
            }

            PdfWriter.getInstance(document,new FileOutputStream("default2.pdf"));
            String path="c:/windows/fonts/simhei.ttf";
            BaseFont bt=BaseFont.createFont(Objects.requireNonNull(fileresource.getFile().getPath())+",0",BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED);
            Font chinesefount=new Font(bt,25f,Font.BOLD, BaseColor.BLACK);
            document.open();
            document.newPage();
            Paragraph caption=new Paragraph("记账凭证",chinesefount);
            caption.setAlignment(Element.ALIGN_CENTER);
            //caption.setFont(chinesefount);
            document.add(caption);
            Paragraph unitriqi=new Paragraph();
            chinesefount.setSize(10f);
            Chunk dangwei=new Chunk("核算单位:陕西三秦都市报社",chinesefount);
            unitriqi.add(dangwei);
            unitriqi.add("                          ");
            DateFormat chinesedate=new SimpleDateFormat("yyyy年MM月dd日");
            String date=chinesedate.format(new Date());
            chinesefount.setSize(10f);
            chinesefount.setStyle(Font.NORMAL);
            Chunk datepara=new Chunk(date,chinesefount);
            datepara.setFont(chinesefount);
            unitriqi.add(datepara);
            document.add(unitriqi);
            Paragraph bianzhidanwei=new Paragraph();
            Chunk  accountunit=new Chunk("核算单位:[009]陕西三秦都市报社",chinesefount);
            String danwei="核算单位:[009]陕西三秦都市报社";
            String code="第1169号-0002/0002";
            bianzhidanwei.add(accountunit);
            String black="";
            Rectangle rectangle=document.getPageSize();
            Float back=rectangle.getWidth()/3-danwei.length()-code.length()-20;
            for(int i=0;i<=80;i++)
            {
                black+="\u00A0";
            }
            bianzhidanwei.add(black);
            Chunk vouchcdoe=new Chunk("第1169号-0002/0002",chinesefount);
            bianzhidanwei.add(vouchcdoe);
            document.add(bianzhidanwei);
            PdfPTable table=new PdfPTable(4);
            PdfPCell cell=new PdfPCell(new Paragraph("摘要",chinesefount));
            table.addCell(cell);
            cell=new PdfPCell(new Paragraph("凭证号",chinesefount));
            table.addCell(cell);
            cell=new PdfPCell(new Paragraph("借方",chinesefount));
            table.addCell(cell);
            cell=new PdfPCell(new Paragraph("贷方",chinesefount));
            document.add(table);




            document.setPageSize(PageSize.A4);

            document.close();




        }
        catch (Exception e) {

        }
    }
}