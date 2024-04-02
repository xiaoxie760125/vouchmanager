package com.sqds.ufdataManager.registory.DataDml;

import com.sqds.Reflect.ReflectUtil;
import com.sqds.springjwtstudy.controller.responsemodel.GzUfModel;
import com.sqds.ufdataManager.model.ufdata.*;
import com.sqds.ufdataManager.registory.ufdata.*;
import com.sqds.ufdataManager.registory.ufsystem.Ua_periodRepository;
import com.sqds.vouchandufdataservice.axinfo;
import com.sqds.vouchandufdataservice.resport;
import com.sqds.vouchdatamanager.model.Department;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
public class Wa_GZDataRepositoryHelp implements Wa_GZDataCustomerRepositoy {
    @PersistenceUnit(unitName = "ufdata")
    private EntityManagerFactory ufdataenfactory;
    @Autowired
    private Ua_periodRepository periodRepository;
   @Autowired
   private personRepository personRepository;
   @Autowired
   private ufpersonRepository ufpersonRepository;
   @Autowired
   private codeRepository codeRepo;
    private List<Object> gzlist;
    private resport<List<axinfo>> columnnames;

    @Override
    public String GetSqlService(vouchcontioan vc, String[] columnnames,ufdatabasebasic info) {
        EntityManager ufdatamanger=this.ufdataenfactory.createEntityManager();
        Query getblset=ufdatamanger.createQuery("select b from wa_gztblset  b where b.iSetGZItemStyle=0");
        List<wa_gztblset> gztblsets=
                getblset.getResultList();
        StringBuilder sqlbuilder=new StringBuilder();
        sqlbuilder.append("select  ");
        sqlbuilder.append(info.getYear()+" as 'iYear',");
        for (String column:columnnames)
        {
            sqlbuilder.append(column+",");
        }
        gztblsets.forEach(s->{

            sqlbuilder.append("sum(F_"+s.getIGzItem_id()+")  as F_"+s.getIGzItem_id());
            if(gztblsets.indexOf(s)!=gztblsets.size()-1)sqlbuilder.append(",");

        });
        sqlbuilder.append(" from wa_gzdata join deparTment on cdepcode=cdept_num  where imonth between ");
        sqlbuilder.append(vc.getBegindate().getMonth().getValue());
        sqlbuilder.append(" and  ");
        sqlbuilder.append(vc.getEnddate().getMonth().getValue());
        if(vc.getCperson_id()!=null&&vc.getCperson_id().length()>0)sqlbuilder.append(" and cpsn_name='"+vc.getCperson_id()+"'");
        sqlbuilder.append(" and btfbz=0");
        sqlbuilder.append(" group by IYear,");
        for (String column:columnnames)
        {

                       sqlbuilder.append(column);
                        if(Arrays.stream(columnnames).toList().indexOf(column)!=columnnames.length-1)sqlbuilder.append(",");
        }


        sqlbuilder.append(" order by IYear,");
        for (String column:columnnames)
        {
            sqlbuilder.append(column);
            if(Arrays.stream(columnnames).toList().indexOf(column)!=columnnames.length-1)sqlbuilder.append(",");
        }
        return sqlbuilder.toString();





    }

    @Override
    public List<Gl_Accvouch> GetGzUfDalModel(vouchcontioan c, GzUfModel gzUfModel, ufdatabasebasic zhangtaohao) {
         StringBuilder sqlbuild=new StringBuilder();
         EntityManager gzentity=this.ufdataenfactory.createEntityManager();
        String ufzhangtao=gzUfModel.getVouchmanager().getUfzhangtao();
        ufdatabasebasic ufinfo=new ufdatabasebasic(ufzhangtao);
        code mdcode=gzUfModel.getMdcode();
        code mccode=gzUfModel.getMccode();
       sqlbuild.append("select ");
        if(mdcode.getBdept() || mccode.getBdept())
        {
            sqlbuild.append(" cdept_num,");
        }
        if (mdcode.getBperson()  || mccode.getBperson()) {
            sqlbuild.append("cpsn_name,");
        }
        sqlbuild.append("sum(");
         for(axinfo colunm:gzUfModel.getColumns())
         {
             sqlbuild.append(colunm.getValue());
             if(gzUfModel.getColumns().indexOf(colunm)<gzUfModel.getColumns().size()-1)sqlbuild.append("+");

         }
         sqlbuild.append(") as sum from wa_gzdata where btfbz=0 and imonth between  ");
         sqlbuild.append(c.getBegindate().getMonth().getValue());
         sqlbuild.append(" and ");
         sqlbuild.append(c.getEnddate().getMonth().getValue());
         if(!gzUfModel.getDepartments().isEmpty())
         {
             sqlbuild.append(" and cdept_num in (");
             for (Department dep:gzUfModel.getDepartments())
             {
                 sqlbuild.append("'"+dep.getCDepCode()+"'");
                 if(gzUfModel.getDepartments().indexOf(dep)!=gzUfModel.getDepartments().size()-1)sqlbuild.append(",");

             }
             sqlbuild.append(")");
         }
         sqlbuild.append(" and ");
        for(axinfo colunm:gzUfModel.getColumns())
        {
            sqlbuild.append(colunm.getValue());
            if(gzUfModel.getColumns().indexOf(colunm)<gzUfModel.getColumns().size()-1)sqlbuild.append("+");

        }
        sqlbuild.append(">0 ");
         if(mdcode.getBperson() || mccode.getBperson())
         {
             sqlbuild.append(" group by cpsn_name");
         }
         if(mccode.getBdept() || mdcode.getBdept())
         {
             sqlbuild.append(" group by cdept_num");
         }
         try {

             Query sumgz=gzentity.createNativeQuery(sqlbuild.toString());

             List<Object> sumgzlist=sumgz.getResultList();
             List<Gl_Accvouch> result=new LinkedList<>();
             final int[] inoid = {1};
             if(sumgzlist.size()>0) {
                 Gl_Accvouch summd = new Gl_Accvouch();
                 summd.setCcode(gzUfModel.getVouchmanager().getMdcode());
                 summd.setCcodeequal(gzUfModel.getVouchmanager().getMccode());
                 summd.setMd(BigDecimal.valueOf(0));
                 if (mdcode.getBitem()) {
                     summd.setCitem_id(gzUfModel.getVouchmanager().getMdaxselect().getValue());
                     summd.setCitem_class(mdcode.getCass_item());
                 }
                 Gl_Accvouch sumcmc = new Gl_Accvouch();
                 sumcmc.setCcode(gzUfModel.getVouchmanager().getMccode());
                 sumcmc.setCcodeequal(gzUfModel.getVouchmanager().getMdcode());
                 sumcmc.setMc(BigDecimal.valueOf(0));
                 if (mccode.getBitem()) {
                     sumcmc.setCitem_id(gzUfModel.getVouchmanager().getMdaxselect().getValue());
                     sumcmc.setCitem_class(mdcode.getCass_item());
                 }
                Object simpname=sumgzlist.get(0);
                 if (sumgzlist.get(0).getClass().getSimpleName().equals("BigDecimal")) {
                     summd.setMd((BigDecimal) sumgzlist.get(0));


                     result.add(summd);
                     inoid[0]++;
                     sumcmc.setInid(inoid[0]);
                     sumcmc.setMc((BigDecimal) sumgzlist.get(0));
                     result.add(sumcmc);




                 }
                 else {

                     for (Object obj : sumgzlist) {
                         Object[] sumgzobj=(Object[])obj;
                         if((((BigDecimal)sumgzobj[1]).intValue()>0)) {

                             if (mdcode.getBperson() || mdcode.getBdept()) {
                                 Gl_Accvouch glAccvouch = getvouchs(mdcode, gzUfModel, sumgzobj, zhangtaohao);
                                  glAccvouch.setCcodeequal(mccode.getCcode());
                                 glAccvouch.setMc(BigDecimal.valueOf(0));
                                 result.add(glAccvouch);
                             } else {
                                 summd.setMd(summd.getMd().add((BigDecimal) sumgzobj[1]));
                             }

                             if (mccode.getBperson() || mccode.getBdept()) {
                                 Gl_Accvouch glAccvouch = getvouchs(mccode, gzUfModel, sumgzobj, zhangtaohao);
                                 glAccvouch.setMc(glAccvouch.getMd());
                                 glAccvouch.setMd(BigDecimal.valueOf(0));
                                 glAccvouch.setCcodeequal(mdcode.getCcode());

                                 result.add(glAccvouch);
                             } else {
                                 sumcmc.setMc(sumcmc.getMc().add((BigDecimal) sumgzobj[1]));
                             }
                         }


                     }
                     if (summd.getMd().intValue() > 0) {
                        summd.setMc(BigDecimal.valueOf(0));
                         result.add(0, summd);
                     }
                     if (sumcmc.getMc().intValue() > 0) {

                          sumcmc.setMd(BigDecimal.valueOf(0));
                         result.add(sumcmc);
                     }


                 }

                 String cdiegest = c.getBegindate().getMonth().getValue() + "月" + gzUfModel.getVouchmanager().getVmname() + "分配";
                 result.forEach(s -> {
                        s.setInid(inoid[0]);
                         if(s.getCperson_id()==null||s.getCperson_id().isEmpty())
                         {
                             s.setCdigest(cdiegest);
                         }
                         else
                         {
                             s.setCdigest(c.getBegindate().getMonth().getValue()+"月" + s.getCdigest());
                         }
                         inoid[0]++;

                 });

             }
             return result;
         }
         catch (Exception e)
         {
             return  null;
         }

    }
    private  Gl_Accvouch getvouchs(code mdcode,GzUfModel gzUfModel,Object[] sumgzobj,ufdatabasebasic zhangtaohao)
    {
        Gl_Accvouch glAccvouch=new Gl_Accvouch();
        glAccvouch.setCcode(mdcode.getCcode());
        if(mdcode.getBdept())
        {
            if(zhangtaohao.getZhangtaohao().equals(gzUfModel.getVouchmanager().getUfzhangtao()))
            {

                glAccvouch.setCdept_id(sumgzobj[0].toString());
            }
            else
            {
                //查找总账部对应的部门编码
                for(int i=0;i<gzUfModel.getDepartments().size()-1;i++)
                {
                    if(gzUfModel.getDepartments().get(i).getCDepCode().equals(sumgzobj[1].toString()))
                    {
                        glAccvouch.setCdept_id(gzUfModel.getUfdepartments().get(i).getCDepCode());
                        break;
                    }
                }

            }



        }
        if(mdcode.getBperson())
        {
            ufdatabasebasic info=new ufdatabasebasic(gzUfModel.getVouchmanager().getUfzhangtao());
            ufperson person=this.ufpersonRepository.findFirstByCPersonName(sumgzobj[0].toString(),info);
            if(person==null)
            {
                person=this.dymicaddperson(sumgzobj[0].toString(),info);

            }
            glAccvouch.setCperson_id(person.getCPersonCode());
            glAccvouch.setCdept_id(person.getCDepCode());
            glAccvouch.setCdigest(gzUfModel.getVouchmanager().getVmname()+"("+person.getCPersonName()+")");
        }
        if(mdcode.getBitem())
        {
            glAccvouch.setCitem_id(gzUfModel.getVouchmanager().getMdaxselect().getValue());
            glAccvouch.setCitem_class(mdcode.getCass_item());
        }
        if(mdcode.getBperson() || mdcode.getBdept())glAccvouch.setMd(BigDecimal.valueOf(Double.valueOf(sumgzobj[1].toString())));
        else {glAccvouch.setMd(BigDecimal.valueOf(Double.valueOf(sumgzobj[0].toString())));}

        return  glAccvouch;

    }
    //动态替换
    private  ufperson dymicaddperson(String personname,ufdatabasebasic info)
    {
        ufperson ufperson=new ufperson();
        ufperson.setCPersonName(personname);
        List<ufperson> personlist=this.ufpersonRepository.findallByPerson_NameOrAndCPsn_Num("",info);
       Optional<ufperson> ps=personlist.stream().max(Comparator.comparingInt(p -> Integer.parseInt(p.getCPersonCode())));
       if(ps.isPresent())
       {
           ufperson.setCPersonCode(String.valueOf(Integer.valueOf(ps.get().getCPersonCode())+1));
           ufperson.setCDepCode(ps.get().getCDepCode());
       }
       this.ufpersonRepository.insert(ufperson.getCPersonCode(),ufperson.getCPersonName(),ufperson.getCDepCode(),info);
        return  ufperson;
    }

    private  String getsqlstr(List<wa_gztblset>  gztblsets,vouchcontioan vc,int year)
    {

        StringBuilder sqlbuilder=new StringBuilder();
        sqlbuilder.append("select  ");
        sqlbuilder.append(year+"  'iYear',imonth ,cPsn_Name,cdepname,");
        gztblsets.forEach(s->{
            sqlbuilder.append("F_"+s.getIGzItem_id());
           // sqlbuilder.append("  '"+s.getCSetGZItemName()+"'");
            if(gztblsets.indexOf(s)!=gztblsets.size()-1)sqlbuilder.append(",");

        });
        sqlbuilder.append("  from wa_gzdata join department on cdepcode=cdept_num where imonth between ");
        sqlbuilder.append(vc.getBegindate().getMonth().getValue());
        sqlbuilder.append("  and  ");
        sqlbuilder.append(vc.getEnddate().getMonth().getValue());
        if(vc.getCperson_id()!=null&&vc.getCperson_id().length()>0)sqlbuilder.append(" and cpsn_num='"+vc.getCperson_id()+"'");
        sqlbuilder.append(" and btfbz=0");
        sqlbuilder.append(" order by imonth,cdept_num");
        return sqlbuilder.toString();
    }
    @Override
     public  <T> List<T> FindGzinfoIsum(vouchcontioan c, String[] columnames, ufdatabasebasic info) throws InvocationTargetException, IllegalAccessException, InstantiationException {
         EntityManager ufdatamanger=this.ufdataenfactory.createEntityManager();
         Query getblset=ufdatamanger.createQuery("select b from wa_gztblset  b where  b.iSetGZItemStyle=0");

         List<wa_gztblset> gztblsets=
                 getblset.getResultList();
         Map<String ,Object> properties=new HashMap<>();
         gztblsets.forEach(s->properties.put("F_"+s.getIGzItem_id(),Float.valueOf(0)));
         Wa_GZData d=(Wa_GZData)ReflectUtil.getObject(new Wa_GZData(),properties);
         //根据条件生成动态sql
         String sqlstr=columnames.length==0?getsqlstr(gztblsets,c,info.getYear()):this.GetSqlService(c,columnames,info);
         Query query=ufdatamanger.createNativeQuery(sqlstr);
         Class<T> GZData= (Class<T>) d.getClass();

         List<T> resultlist=new ArrayList<>();
         List dlist=query.getResultList();
         String[] inputcolumns=columnames.length==0?new String[4]:new String[columnames.length+1];
         inputcolumns[0]="IYear";
         if(columnames.length==0) {
             inputcolumns[1] = "IMonth";
             inputcolumns[2] = "CPsn_Name";
             inputcolumns[3] = "cdepname";
         }
         else
         {

            inputcolumns[0]="IYear";
             for(int i=0;i<columnames.length;i++)
             {

                 inputcolumns[i+1]=!columnames[i].equals("cdepname")?(columnames[i].substring(0,2).toUpperCase()+columnames[i].substring(2,columnames[i].length())):"cdepname";
             }

         }
         T sum=(T)GZData.getDeclaredConstructors()[0].newInstance();
         dlist.forEach(s->{
             try {
                 T b=(T)GZData.getDeclaredConstructors()[0].newInstance();
                 Field[] fields=GZData.getDeclaredFields();
                 Object[] obj=(Object[])s;
                 for(int i=0;i<obj.length;i++){

                     String filed;
                     if(i<inputcolumns.length)
                     {
                         filed=inputcolumns[i];
                     }
                     else
                     {
                         filed="F_"+gztblsets.get(i-inputcolumns.length).getIGzItem_id();
                     }
                     try {

                         Field ds=GZData.getDeclaredField("$cglib_prop_"+filed);
                         ds.setAccessible(true);
                         switch (ds.getType().getSimpleName())
                         {
                             case "String":
                                 ds.set(b,obj[i].toString());
                                 break;
                             case "Integer":
                                 ds.set(b,Integer.parseInt(obj[i].toString()));
                                 break;
                             case "double":
                                 ds.set(sum,(Double)ds.get(sum)+Double.parseDouble(obj[i].toString()));
                                 ds.set(b,Double.parseDouble(obj[i].toString()));
                                 break;
                             case "float":
                             case "Float":
                                 ds.set(sum,(ds.get(sum)==null?0:(Float)ds.get(sum))+(obj[i]!=null?Float.parseFloat(obj[i].toString()):0));
                                 ds.set(b,obj[i]!=null?Float.parseFloat(obj[i].toString()):0);
                                 break;
                         }
                         //ds.set(b,obj[i]);

                     }
                     catch (Exception e)
                     {
                         System.out.println(e.getMessage());
                     }

                 }
                 resultlist.add(b);
             }  catch (InstantiationException e) {
                 throw new RuntimeException(e);
             } catch (IllegalAccessException | InvocationTargetException e) {
                 throw new RuntimeException(e);
             }

         });


         resultlist.add(sum);



         return resultlist;
     }
    @Override
    public <T> List<T> FindGzinfo(vouchcontioan c, ufdatabasebasic info) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        return  this.FindGzinfoIsum(c,new String[]{"cdepcode","cdepname","CPsn_Name"},info);

    }

    @Override
    public <T> List<T> FindGzinfo(vouchcontioan c, String[] columnames, ufdatabasebasic info) throws InvocationTargetException, IllegalAccessException {
        return null;
    }

    @Override
    public List<axinfo> getcolums(String[] columnames, boolean isorg,ufdatabasebasic info) throws InvocationTargetException, IllegalAccessException {
       List<axinfo> resultlist=new ArrayList<>();
       String Filed_profix=!isorg?"$cglib_prop_":"";
       resultlist.add(new axinfo("年", Filed_profix+"iyear"));
       resultlist.add(new axinfo("月",Filed_profix+"imonth"));
       resultlist.add(new axinfo("姓名",Filed_profix+"cpsn_Name"));
       resultlist.add(new axinfo("部门",Filed_profix+"cdepname"));
       List<String> cols=new ArrayList<>();

       if(info.getYear()==0)
       {
           info.setYear(periodRepository.findMaxPeriodByCAcc_id(info.getZhangtaohao()));
       }
       EntityManager em=this.ufdataenfactory.createEntityManager();
       Query gettblset=em.createQuery("select tb from wa_gztblset  tb where tb.iSetGZItemProp<2 order by tb.iGZNum"  );

       List<wa_gztblset> tblsetlist=gettblset.getResultList();
       if(Arrays.stream(columnames).count()==0)
       {
           tblsetlist.forEach(s->{
                              resultlist.add(new axinfo(s.getCSetGZItemName(),Filed_profix+"f_"+s.getIGzItem_id()));
           });
       }
       else {
           cols= Arrays.stream(columnames).toList().subList(3,columnames.length);
           cols.forEach(s -> {
               Optional<wa_gztblset> sr = tblsetlist.stream().filter(t -> t.getCSetGZItemName().equals(s)).findFirst();
               if (sr.isPresent()) {
                   resultlist.add(new axinfo(sr.get().getCSetGZItemName(), Filed_profix + "f_" + sr.get().getIGzItem_id()));
               } else {
                   System.out.println(s);
               }


           });
       }
       return resultlist;
    }

    /**
     * Excel导入操作
     * @param file
     * @param info
     * @return
     */
    @Transactional
   public vouchcontioan  insertgzdatafromExcelFile(File file, ufdatabasebasic info) {
        StringBuilder stringBuilder=new StringBuilder();
        EntityManager entityManager=this.ufdataenfactory.createEntityManager();
        EntityTransaction transaction=entityManager.getTransaction();
        try {
            Workbook workbook= WorkbookFactory.create(file);
            Sheet sheet=workbook.getSheetAt(0);
            Row caption=sheet.getRow(0);
            String[] captions=new String[caption.getLastCellNum()];
            for(int i=0;i<caption.getLastCellNum();i++)
            {
                captions[i]=caption.getCell(i).getStringCellValue();
            }
            vouchcontioan vc=new vouchcontioan();
            Integer year=(int) sheet.getRow(1).getCell(0).getNumericCellValue();
            Integer month=(int) sheet.getRow(1).getCell(1).getNumericCellValue();
            vc.setBegindate(LocalDateTime.of(year,month,2,0,0,0));
            vc.setEnddate(LocalDateTime.of(year,month,20,0,0,0));

            //修给导入项目

            List<axinfo> axinfos=this.getcolums(captions,true,info).stream().filter(s->!s.getTitle().equals("部门")).toList();
            int lastnum=sheet.getLastRowNum();                    ;
              for(int i=1;i<sheet.getLastRowNum();i++)
              {
                  caption=sheet.getRow(i);
                  if(caption==null || caption.getCell(0)==null){
                      i++;
                     break;
                  };
                  Query query=entityManager.createQuery("select gzdata from Wa_GZData  gzdata where gzdata.cPsn_Name=:psnname " +
                          " and gzdata.iYear=:year and gzdata.iMonth=:month and gzdata.bTFBZ=false ")
                          .setParameter("psnname",sheet.getRow(i).getCell(2).getStringCellValue())
                          .setParameter("year",sheet.getRow(i).getCell(0).getCellType()==Cell.CELL_TYPE_NUMERIC?sheet.getRow(i).getCell(0).getNumericCellValue():sheet.getRow(i).getCell(0).getStringCellValue())
                          .setParameter("month",sheet.getRow(i).getCell(1).getCellType()==Cell.CELL_TYPE_NUMERIC?sheet.getRow(i).getCell(1).getNumericCellValue():sheet.getRow(i).getCell(1).getStringCellValue());
                  List<Wa_GZData> gzdatas=query.getResultList();
                  person p = personRepository.dynamicgetgzperson(sheet.getRow(i).getCell(2).getStringCellValue(), info);
                 /* if(p==null)
                  {

                     p=this.personRepository.dynamicgetgzperson(sheet.getRow(i).getCell(2).getStringCellValue(), info);
                  }*/
                  if(gzdatas.isEmpty()) {

                      stringBuilder.append("insert into wa_gzdata(cGZGradeNum,cPsn_Num,cdept_num,");
                      axinfos.forEach(s -> {
                          stringBuilder.append(s.getValue());
                          if (axinfos.indexOf(s) < axinfos.size() - 1) stringBuilder.append(",");
                      });
                      stringBuilder.append(")  values('001',");
                      stringBuilder.append("'"+p.getCPsn_Num() + "',");
                      stringBuilder.append("'"+p.getCDept_num() + "',");
                      stringBuilder.append((sheet.getRow(i).getCell(0).getCellType() == Cell.CELL_TYPE_NUMERIC ? sheet.getRow(i).getCell(0).getNumericCellValue() : sheet.getRow(i).getCell(0).getStringCellValue()) + ",");
                      stringBuilder.append((sheet.getRow(i).getCell(1).getCellType() == Cell.CELL_TYPE_NUMERIC ? sheet.getRow(i).getCell(1).getNumericCellValue() : sheet.getRow(i).getCell(1).getStringCellValue()) + ",");
                      stringBuilder.append("'"+p.getCPsn_Name() + "',");
                      for (int col = 3; col < axinfos.size(); col++) {
                          stringBuilder.append((sheet.getRow(i).getCell(col).getCellType() == Cell.CELL_TYPE_NUMERIC ? sheet.getRow(i).getCell(col).getNumericCellValue() : sheet.getRow(i).getCell(col).getStringCellValue()) );
                          if (col < axinfos.size() - 1) {
                              stringBuilder.append(",");
                          }


                      }
                      stringBuilder.append(")");
                  }
                  else
                  {
                      stringBuilder.append("update wa_gzdata set ");
                      for (int col = 3; col<axinfos.size(); col++) {
                          stringBuilder.append(axinfos.get(col).getValue() + "=" + (sheet.getRow(i).getCell(col).getCellType() == Cell.CELL_TYPE_NUMERIC ? sheet.getRow(i).getCell(col).getNumericCellValue() : sheet.getRow(i).getCell(col).getStringCellValue()) );
                          if (col < axinfos.size() - 1) {
                              stringBuilder.append(",");
                          }

                      }
                      stringBuilder.append(" where iyear=");
                      stringBuilder.append((sheet.getRow(i).getCell(0).getCellType() == Cell.CELL_TYPE_NUMERIC ? sheet.getRow(i).getCell(0).getNumericCellValue() : sheet.getRow(i).getCell(0).getStringCellValue()) + " and ");
                      stringBuilder.append("imonth=");
                      stringBuilder.append((sheet.getRow(i).getCell(1).getCellType() == Cell.CELL_TYPE_NUMERIC ? sheet.getRow(i).getCell(1).getNumericCellValue() : sheet.getRow(i).getCell(1).getStringCellValue()) + " and ");
                      stringBuilder.append("cpsn_num='");
                      stringBuilder.append(p.getCPsn_Num()+"'");


                  }
                      //添加换行符
                      stringBuilder.append("\n");





              }
              Query insertorupdateseql=entityManager.createNativeQuery(stringBuilder.toString());

             transaction.begin();
              int result= insertorupdateseql.executeUpdate();
              transaction.commit();
             return vc;

        }
        catch (Exception e)
        {
             return null;
        }



    }



    /**
     * 将工资数据导入到Excel
     * @param gzlist
     * @param columnnames
     *
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public <T> Workbook getworkbook(List<T> gzlist, resport<List<LinkedHashMap<String,String>>> columnnames, ufdatabasebasic info) throws NoSuchFieldException, IllegalAccessException {

        try {
            Workbook workbook= new HSSFWorkbook();
            Sheet sheet=workbook.createSheet(columnnames.getKey());
            CellStyle style=workbook.createCellStyle();
            style.setBorderBottom(CellStyle.BORDER_DOTTED);
            style.setBorderLeft(CellStyle.BORDER_THIN);
            style.setBorderRight(CellStyle.BORDER_THIN);
            style.setBorderTop(CellStyle.BORDER_THIN);
            style.setAlignment(CellStyle.ALIGN_CENTER);
            style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

            Font font=workbook.createFont();
            font.setFontName("Arial");

            font.setFontHeightInPoints((short)12);
            style.setFont(font);
            //创建表头
            Row captionrow=sheet.createRow(0);
            for (int i=0;i<columnnames.getValue().size();i++)
            {
                Cell cell=captionrow.createCell(i);
                cell.setCellValue(columnnames.getValue().get(i).get("title"));
                cell.setCellStyle(style);


            }
            Class<?> type=gzlist.get(0).getClass();
            font.setFontHeightInPoints((short) 10);
            style.setFont(font);

            //创建数据行
            for (int i=0;i<gzlist.size();i++)
            {
                captionrow=sheet.createRow(i+1);
                LinkedHashMap<String,Object> valus=(LinkedHashMap<String,Object>)gzlist.get(i);
                for (int j=0;j<columnnames.getValue().size();j++)
                {
                    Cell value=captionrow.createCell(j);
                    String key=columnnames.getValue().get(j).get("value");
                    if(valus.get(key)==null)
                    {
                        value.setCellValue("");
                    }
                    else {
                        switch (valus.get(key).getClass().getSimpleName()) {
                            case "Integer":
                                value.setCellValue((Integer) valus.get(key));
                                break;
                            case "Double":
                                if ((Double) valus.get(key) == 0) {
                                    value.setCellValue("");
                                } else {
                                    value.setCellValue((Double) valus.get(key));
                                }
                                break;
                            default:
                                value.setCellValue(valus.get(key).toString());
                                break;
                        }
                    }

                    value.setCellStyle(style);


                }

                //工资表格的格式化







            }

          /*  captionrow=sheet.createRow(sheet.getLastRowNum()+1);
            Cell cell=captionrow.createCell(0);
            cell.setCellValue("合计");
            for(int j=3;j<columnnames.getValue().size();j++)
            {
                char num=(char)(97+j);
                Cell value=captionrow.createCell(j);
                value.setCellFormula("sum("+num+"2:"+num+sheet.getLastRowNum()+")");

                value.setCellStyle(style);
            }*/
            return workbook;

        }
        catch (Exception e)
        {
            return new HSSFWorkbook();
        }

    }

    @Override
    public List<com.sqds.ufdataManager.model.ufdata.Department> getdeptment(String code, ufdatabasebasic info) {
        EntityManager entityManager=this.ufdataenfactory.createEntityManager();
        Query selectdepartment=entityManager.createQuery("select d from Department d where d.cDepCode=:code or :code is null or :code=''");
        selectdepartment.setParameter("code",code);
        return selectdepartment.getResultList();

    }

    /**
     * 工资任务的计算
     * @param vc
     * @param info
     * @return
     */
    @Transactional
    @Override
   public   List<String> computerFormula(vouchcontioan vc, ufdatabasebasic info)
    {
        EntityManager formulaentityManager=this.ufdataenfactory.createEntityManager();
        EntityTransaction transaction=formulaentityManager.getTransaction();
        final Query[] fromula = {formulaentityManager.createQuery("select f from WA_formula f ")};
        List<WA_formula> formulas= fromula[0].getResultList();
        fromula[0] =formulaentityManager.createQuery("select f from wa_gztblset f");
        List<wa_gztblset> gzitem= fromula[0].getResultList();
        List<String> result=new LinkedList<>();
        try {
            for (WA_formula f : formulas) {
                String translateformul = "F_" + f.getIGzitem_id() + "=";
                String itempar = "([\\(\\+\\-\\*/]?)(?<item>[\\u4e00-\\u9fa5]{2,10})";
                Pattern pattern = Pattern.compile(itempar);
                Matcher matcher = pattern.matcher(f.getCGZItemFromula());
                List<wa_gztblset> needformlu = new LinkedList<>();
                while (matcher.find()) {
                    String itemname = matcher.group("item");
                    wa_gztblset ns = gzitem.stream().filter(s -> s.getCSetGZItemName().equals(itemname)).findFirst().orElse(null);
                    if (ns != null && !needformlu.contains(ns)) {
                        needformlu.add(ns);
                    }

                }
                needformlu=sortgztblset(needformlu);
               // needformlu.sort((s1, s2) -> s1.getCSetGZItemName().contains(s2.getCSetGZItemName()) ? -1 : 1);
                //List<wa_gztblset> needformlu=gzitem.stream().filter(s->f.getCGZItemFromula().contains(s.getCSetGZItemName())).toList();
                String tranformula = f.getCGZItemFromula();

                for (wa_gztblset s : needformlu) {
                    tranformula = tranformula.replace(s.getCSetGZItemName(), "F_" + s.getIGzItem_id());
                }
                if (tranformula.contains("iff(")) {

                    String[] formulastr = tranformula.split("iff\\(");
                    int formcap = formulastr.length - 1;
                    final String[] tmpformul = {" case when"};
                    Arrays.stream(formulastr).filter(s -> s.contains(",")).forEach(s -> {
                        String[] fsr = s.split(",");
                        tmpformul[0] += " " + fsr[0];

                        tmpformul[0] += " Then " + fsr[1];
                        if (fsr.length == 3) {
                            //去掉公示的后括号
                            fsr[2] = fsr[2].substring(0, fsr[2].length() - formcap);
                        }
                        tmpformul[0] += " Else " + (fsr.length == 3 ? fsr[2] + " " : " Case When ");


                    });
                    tranformula = tmpformul[0];
                    for (int i = 0; i < formcap; i++) {
                        tranformula += " end ";
                    }
                    tranformula = tranformula.replace("\"", "'");

                    System.out.println(tranformula);

                }
                ;
                translateformul += tranformula;
                translateformul = "update wa_gzdata set " + translateformul + " where imonth between " + vc.getBegindate().getMonth().getValue()
                        + " and " + vc.getEnddate().getMonth().getValue() + " and btfbz=0";
                Query updatequer = formulaentityManager.createNativeQuery(translateformul);
                transaction.begin();
                updatequer.executeUpdate();
                transaction.commit();

                result.add(translateformul);

            }
        }
        catch (Exception e)
        {
            result.add(e.getMessage());
        }
        finally {
            formulaentityManager.close();
        }

     //中文正则表达式


        return  result;

    }
    private  List<wa_gztblset> sortgztblset(List<wa_gztblset> gztblsetlist)
    {
        for(int i=0;i<gztblsetlist.size();i++)
        {
            wa_gztblset needsord=gztblsetlist.get(i);
            for (int j=i+1;j<gztblsetlist.size();j++)
            {

                wa_gztblset needcompare=gztblsetlist.get(j);
                if(needcompare.getCSetGZItemName().contains(needsord.getCSetGZItemName()))
                {
                    gztblsetlist.set(i, needcompare);
                    gztblsetlist.set(j, needsord);
                    needsord=needcompare;
            }
            }
        }
        return  gztblsetlist;

    }

}
