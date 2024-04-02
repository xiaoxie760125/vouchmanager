package com.sqds.ufdataManager.model.ufdata;

import com.sqds.filelutil.excelutil.ExceLTitle;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 凭证表
 */
@Entity
@Data
@Table
public class Gl_Accvouch implements  Cloneable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Integer i_id;
    private  Integer iperiod;
    @ExceLTitle(title ="凭证号")
    private  Integer ino_id;
    @ExceLTitle(title ="日期")
    private Date dbill_date;
    @ExceLTitle(title ="附件张数")
    private  Integer idoc;
    @ExceLTitle(title ="制单人")
    private String cbill;
    private  String ccheck;
    @Getter
    private  Boolean ibook=false;
   private  Integer inid;
    private  Boolean iflag;
    @ExceLTitle(title ="摘要")
    private  String cdigest;
    @ExceLTitle(title ="科目编码")
    private  String ccode;
    @Transient
    private  String ccodename;
    @Column(name ="ccode_equal")
    private String ccodeequal;
    @Transient
    @ExceLTitle(title ="期初余额")
    private  BigDecimal mb;
    @ExceLTitle(title ="借方发生额")
    private BigDecimal md;
    @ExceLTitle(title ="贷方发生额")
    private BigDecimal mc;
    @Transient
    @ExceLTitle(title ="方向")
    private String ccend_c;
    @Transient
    @ExceLTitle(title ="余额")
    private  BigDecimal me;
    @ExceLTitle(title ="部门")
    private  String cdept_id;
    @ExceLTitle(title = "人员")
    private  String cperson_id;
    @ExceLTitle(title = "客户")
    private String ccus_id;
    @ExceLTitle(title ="供应商")
    private  String csup_id;
    @ExceLTitle(title = "项目")
    private  String citem_id;
    private String citem_class;
    private  String csign;
    private  Integer isignseq;

    @Override
    public Gl_Accvouch clone(){
        Gl_Accvouch accvouch=new Gl_Accvouch();
        try {
            accvouch.setCdigest(this.getCdigest());
            accvouch.setIdoc(this.idoc);
            accvouch.setCbill(this.cbill);
            accvouch.setIno_id(this.ino_id);
            accvouch.setInid(this.inid+1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return accvouch;
    }

}
