package com.sqds.vouchandufdataservice;

import com.sqds.comutil.RedisUtil;
import com.sqds.springjwtstudy.controller.responsemodel.GzUfModel;
import com.sqds.ufdataManager.model.ufdata.Gl_Accvouch;
import com.sqds.ufdataManager.model.ufdata.code;
import com.sqds.ufdataManager.model.ufdata.person;
import com.sqds.ufdataManager.registory.DataDml.Gl_AccVouchRepositoryHelp;
import com.sqds.ufdataManager.registory.DataDml.ufdatabasebasic;
import com.sqds.ufdataManager.registory.ufdata.Gl_AccVouchRepository;
import com.sqds.ufdataManager.registory.ufdata.codeRepository;
import com.sqds.ufdataManager.registory.ufdata.personRepository;
import com.sqds.ufdataManager.registory.ufdata.vouchcontioan;
import com.sqds.ufdataManager.registory.ufsystem.Ua_periodRepository;
import com.sqds.vouchdatamanager.Help.CodeUtil;
import com.sqds.vouchdatamanager.model.newsvouchs;
import com.sqds.vouchdatamanager.model.vouchs;
import com.sqds.vouchdatamanager.registroy.PersonRepsitory;
import com.sqds.vouchdatamanager.registroy.vouchsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class Insertufdatavouchs {
    @Autowired
    Gl_AccVouchRepository gl_vouchmannager;
    @Autowired
   PersonRepsitory personRepository;
    @Autowired
    personRepository ufdatapersonrepository;
    @Autowired
    codeRepository ufcode;
    @Autowired
    @Qualifier("ufdatainfo")
    ufdatabasebasic info;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    Ua_periodRepository period;
    @Autowired
    Basicinfo basicinfo;
    @Autowired
    vouchsRepository vouchsmanager;
    @Autowired
    CodeUtil codeUtil;


    private  List<person> s=null;
    private person  getperson(String zhantaohao,String psn_num)
    {
        if(this.info.getZhangtaohao()!=zhantaohao)
        {
            this.info.setZhangtaohao(zhantaohao);
            int year=this.period.findMaxPeriodByCAcc_id(zhantaohao);
            this.info.setYear(year);
        }
        s=basicinfo.getpersonlist("",info);
        return s.stream().filter(p->p.getCPsn_Num().equals(psn_num)).findFirst().get();

       /* if(info.getZhangtaohao()!=zhantaohao)
        {
            info.setZhangtaohao(zhantaohao);
            int year=period.findMaxPeriodByCAcc_id(zhantaohao);
            info.setYear(year);
        }
        return  ufdatapersonrepository.findFirstByCPersonCode(psn_num,info);
*/
    }
    vouchtoufdata<newsvouchs> getufdata=new vouchtoufdata<newsvouchs>() {
        @Override
        public List<Gl_Accvouch> getGl_Accvouch(List<newsvouchs> newsvouchs, vouchtoglmodel vouchmodel) {

            List<Gl_Accvouch> result = new LinkedList<>();
            Gl_Accvouch sumvouch=new Gl_Accvouch();
            sumvouch.setMd(BigDecimal.valueOf(0));
            sumvouch.setMc(BigDecimal.valueOf(0));
            person ufdataperson=null;
            int inid=1;
            //自定辅助项目
            Map<String,axinfo> axinfso=new HashMap<>();
            for (newsvouchs newsvouch : newsvouchs) {
                Gl_Accvouch gl_vouch = new Gl_Accvouch();
                int vouchyear=newsvouch.getVouchdate().getYear();
                if(vouchyear>period.findMaxPeriodByCAcc_id(vouchmodel.getVouchmanager().getUfzhangtao()))
                {
                    vouchyear=period.findMaxPeriodByCAcc_id(vouchmodel.getVouchmanager().getUfzhangtao());
                }
                //从工资账套获取账套信息
                person nperson = getperson(vouchmodel.getUfpzhangtao(), newsvouch.getCpsn_num());
                 ufdataperson=new person();
              

                if(nperson!=null)
                {
                    info.setZhangtaohao(vouchmodel.vouchmanager.getUfzhangtao());

                    ///从账套账套获取人员明细
                    ufdataperson=ufdatapersonrepository.findFirstByCPersonName(nperson.getCPsn_Num(),info)==null?nperson:ufdatapersonrepository.findFirstByCPersonName(nperson.getCPsn_Num(),info);
                    axinfso.put("person", new axinfo(ufdataperson.getCPsn_Num(),ufdataperson.getCPsn_Num()));
                    
                }
                String cdigest =vouchmodel.getVouchmanager().getMccode().startsWith("100")?"付":(vouchmodel.getVouchmanager().getMdcode().startsWith("100")?"收":"结算");
                if (nperson != null) {
                    cdigest = cdigest + nperson.getCPsn_Name();
                }
                BigDecimal md = newsvouch.getShishou();
                BigDecimal tax = BigDecimal.valueOf(0);
                if (vouchmodel.getVouchmanager().getMdtaxcode()!=null) {
                    tax =newsvouch.getTax().equals(BigDecimal.valueOf(0))?md.divide(BigDecimal.valueOf(1 + vouchmodel.getVouchmanager().getTaxvalue())).multiply(BigDecimal.valueOf(vouchmodel.getVouchmanager().getTaxvalue())):newsvouch.getTax();
                    md =newsvouch.getTax().equals(BigDecimal.valueOf(0))?md.subtract(tax):newsvouch.getRevenue();
                }
                else
                {
                    md=newsvouch.getShishou();
                    tax=BigDecimal.valueOf(0);
                }
                //添加动态辅助项目逻辑
                codeUtil.setcodeaxinfo(gl_vouch,vouchmodel.getVouchmanager().getMdcode(),vouchmodel.getMdaxinfo()==null?axinfso:vouchmodel.getMdaxinfo(),info);
                //添加itemclass
                code codes=ufcode.findFirstByCcode(vouchmodel.getVouchmanager().getMdcode(),info);
//                if(codes.getBitem()==true)
//                {
//
//                    gl_vouch.setCitem_class(codes.getCass_item());
//                }*/
                if(vouchmodel.getYear()!=0)cdigest+=String.valueOf(vouchmodel.getYear())+"年";
                cdigest += newsvouch.getCustomername() + vouchmodel.getVouchmanager().getVmname() +"款(票号:" + newsvouch.getVouchcode() + ")";
                gl_vouch.setCdigest(cdigest);
                gl_vouch.setInid(inid);
                if(codes.getBperson() || codes.getBdept()) {
                      if(codes.getBperson() && nperson!=null)
                      {
                       
                       
                      }

                    if(codes.getBperson())gl_vouch.setCperson_id(ufdataperson != null ? ufdataperson.getCPsn_Num() : null);
                    gl_vouch.setCdept_id(ufdataperson != null ? ufdataperson.getCDept_num() : null);
                }
                gl_vouch.setCcode(vouchmodel.getVouchmanager().getMdcode());
                gl_vouch.setCcodeequal(vouchmodel.getVouchmanager().getMccode());
                gl_vouch.setIdoc(vouchmodel.getIdoc());
                gl_vouch.setCbill(vouchmodel.getCbill());
                gl_vouch.setMc(BigDecimal.valueOf(0));
                //添加借方凭证的逻辑
                if (vouchmodel.getUfmy() == ufvouchsumtype.md) {
                    sumvouch.setMd(sumvouch.getMd().add(md));
                    sumvouch.setCitem_class(gl_vouch.getCitem_class());
                    sumvouch.setCitem_id(gl_vouch.getCitem_id());

                } else {
                    gl_vouch.setMd(md);
                    result.add(gl_vouch);
                }
                //添加借方税金的逻辑
                if(vouchmodel.getVouchmanager().getMdtaxcode()!=null && !vouchmodel.getVouchmanager().getMdtaxcode().isEmpty())
                {
                    Gl_Accvouch mdtax=gl_vouch.clone();
                    mdtax.setCcode(vouchmodel.getVouchmanager().getMdtaxcode());
                    mdtax.setCcodeequal(vouchmodel.getVouchmanager().getMccode());
                    mdtax.setMd(tax);
                    mdtax.setMc(BigDecimal.valueOf(0));
                    result.add(mdtax);
                }
                //结算贷方税额
                if (vouchmodel.getVouchmanager().getMctaxcode()!=null) {
                    tax =newsvouch.getTax().equals(BigDecimal.valueOf(0))?md.divide(BigDecimal.valueOf(1 + vouchmodel.getVouchmanager().getTaxvalue())).multiply(BigDecimal.valueOf(vouchmodel.getVouchmanager().getTaxvalue())):newsvouch.getTax();
                    md =newsvouch.getTax().equals(BigDecimal.valueOf(0))?md.subtract(tax):newsvouch.getRevenue();
                }
                else
                {
                    md=newsvouch.getShishou();
                    tax=BigDecimal.valueOf(0);
                }
                Gl_Accvouch glmc=gl_vouch.clone();

                glmc.setMd(BigDecimal.valueOf(0));
                //添加贷方辅助项目
                codeUtil.setcodeaxinfo(glmc,vouchmodel.getVouchmanager().getMccode(),vouchmodel.getMcaxinfo()==null?axinfso:vouchmodel.getMcaxinfo(),info);
                codes=ufcode.findFirstByCcode(vouchmodel.getVouchmanager().getMccode(),info);
                if(codes.getBitem())glmc.setCitem_class(codes.getCass_item());

                glmc.setCcode(vouchmodel.getVouchmanager().getMccode());
                glmc.setCcodeequal(vouchmodel.getVouchmanager().getMdcode());
                if(vouchmodel.ufmy==ufvouchsumtype.mc)
                {

                    sumvouch.setCitem_class(glmc.getCitem_class());
                    sumvouch.setCitem_id(glmc.getCitem_id());
                    sumvouch.setMc(sumvouch.getMc().add(md));

                }
                else {
                    glmc.setMc(md);
                    result.add(glmc);
                }
                if (vouchmodel.getVouchmanager().getMctaxcode()!=null) {
                    Gl_Accvouch gltax = glmc.clone();
                    gltax.setCcode(vouchmodel.getVouchmanager().getMctaxcode());
                    gltax.setMc(tax);
                    gltax.setMd(BigDecimal.ZERO);
                    result.add(gltax);
                }
            }
            //如果是借方汇总生成汇总数据
            if (vouchmodel.ufmy!= ufvouchsumtype.nosum) {
                StringBuilder sumdigest = new StringBuilder();
                sumdigest.append(vouchmodel.getVouchmanager().getMdcode().startsWith("100")?"收":"付");
                sumdigest.append(ufdataperson.getCPsn_Name());
                sumdigest.append(vouchmodel.getYear());
                sumdigest.append(newsvouchs.get(0).getCustomername());
                if(newsvouchs.size()>1) {
                    sumdigest.append("等");
                    sumdigest.append(newsvouchs.size() + "家" + vouchmodel.getVouchmanager().getVmname() + "款");
                }
                else {
                    sumdigest.append(vouchmodel.getVouchmanager().getVmname());
                }
                sumvouch.setCbill(vouchmodel.getCbill());
                sumvouch.setIdoc(vouchmodel.getIdoc());
                if(vouchmodel.ufmy==ufvouchsumtype.md) {
                    sumvouch.setMc(BigDecimal.valueOf(0));
                }
                else
                {
                    sumvouch.setMd(BigDecimal.valueOf(0));
                }
                sumvouch.setCdigest(sumdigest.toString());
                sumvouch.setCcode(vouchmodel.ufmy==ufvouchsumtype.md?vouchmodel.getVouchmanager().getMdcode():vouchmodel.getVouchmanager().getMccode());
                sumvouch.setCcodeequal(vouchmodel.ufmy==ufvouchsumtype.md?vouchmodel.getVouchmanager().getMccode():vouchmodel.getVouchmanager().getMdcode());
                codeUtil.setcodeaxinfo(sumvouch, axinfso, info);
                int addinfo=(vouchmodel.ufmy==ufvouchsumtype.md)?0:result.size();
                result.add(addinfo, sumvouch);
            }
            return result;
        }


    };

    /**
     *从发票动态生成凭证的逻辑
     * @param vouchs
     * @param para
     * @return
     */
    public  String addpvouchsofudata(List<vouchs> vouchs, vouchtoglmodel para)
    {
        try
        {
            //生成凭证后更新凭证号
            Thread updatevouchno=new Thread(new Runnable(){
               @Override
               public void  run()
               {

                   vouchsmanager.addvouchs(vouchs,String.valueOf(para.getYear()));

               }
            });

            info.setZhangtaohao(para.getVouchmanager().getUfzhangtao());
            int yaer=this.period.findMaxPeriodByCAcc_id(para.getVouchmanager().getUfzhangtao());
            info.setYear(yaer);
            List<Gl_Accvouch> glAccvouches=this.vouchsmanager.fromvouchstogl(vouchs,para ,String.valueOf(para.getYear()));
            String result= this.gl_vouchmannager.Insertvouch(info,glAccvouches,para.getIsfirstvouch());
            String ufdatpathon="\\d{4}年\\d{1,2}月\\d{1,4}号";
            Pattern pattern=Pattern.compile(ufdatpathon);
            if(pattern.matcher(result).find()) {
                vouchs.forEach(s ->{
                    //设置凭证已使用状态
                    s.setIsused(true);
                    s.setUfvouchcode(result);});

                updatevouchno.start();
            }
            return  result;

        }
        catch (Exception E)
        {
            return  E.getMessage();
        }

    }

    public  String addvouchtofudata(List<newsvouchs> vouchs,vouchtoglmodel para)
    {
        try
        {
            info.setZhangtaohao(para.getVouchmanager().getUfzhangtao());
            info.setYear(vouchs.get(0).getVouchdate().getYear());
            List<Gl_Accvouch> gl_vouchs = new LinkedList<>();
            gl_vouchs= getufdata.getGl_Accvouch(vouchs,para);
            if(para.getAxinfo()!=null) {
                getufdata.getGl_Accvouchaxinfo(gl_vouchs.stream().filter(s -> getufdata.isaxsun(ufcode, s.getCcode(),info)).toList(), para.getAxinfo());
            }
            return  this.gl_vouchmannager.Insertvouch(info,gl_vouchs,para.getIsfirstvouch());

        }
        catch (Exception e)
        {
            return  e.getMessage();
        }
    }


    /**
     * 动态查询会计科目列表
     * @param info
     * @param code
     * @return
     */
    public List<code> getCodeList(ufdatabasebasic info,String code)
    {
        return  gl_vouchmannager.coderesult(info,code);
    }

    /**
     * 按照科目差对应辅助信息列表
     * @param info
     * @param code
     * @return
     */
    public  Map<String,List<Gl_AccVouchRepositoryHelp.axinfo>> getcodeinfo(ufdatabasebasic info, String code)
    {
        return  gl_vouchmannager.getaxinfofromCode(info,code);
    }

    /**
     * 从reis缓存在取得类型凭证设置
     * @param key
     * @return
     */
    public  vouchtoglmodel getvouchmanager(String key)
    {
        String listkey=key.substring(0,3);
        String modelkey= key.substring(3,key.length());
        List<vouchtoglmodel> vouchmodels= (List<vouchtoglmodel>)redisUtil.get(listkey);
        vouchtoglmodel vouchtoglmodel=vouchmodels.stream().filter(s -> s.getKey().equals(modelkey)).findFirst().orElse(null);

        return vouchtoglmodel;
    }

    /**
     * 设置凭证设置
     * @param key
     * @param vouchglmodel
     */
    public  void  setvouchglmodel(String key,vouchtoglmodel vouchglmodel) throws NoSuchFieldException, IllegalAccessException {
        
        vouchglmodel.setKey(key);
        String nkey=vouchglmodel.getUfpzhangtao()+vouchglmodel.getVouchmanager().getMccode()+vouchglmodel.getVouchmanager().getMdcode();
        redisUtil.setlist(key,vouchglmodel);
    }

    /**
     * 设置工资分配凭证设置
     * @param key
     * @param ufmodel
     */
    public  void  setgzufmode(String key, GzUfModel ufmodel)
    {
        try {
            redisUtil.setlist(key,ufmodel);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public  List<GzUfModel> getgzufmode(String key)  {
        try {
            return redisUtil.getlist(key);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    //设置报表样式缓存
   public  void  setgzreport(String key,List<axinfo> reportedit,String reportname,boolean isgroup,String[] columns) throws NoSuchFieldException, IllegalAccessException {
       resport gzreport=new resport(reportname,reportedit,isgroup);
       gzreport.setColumns(columns);
       gzreport.setKey(reportname);

       try {

           redisUtil.setlist(key,gzreport);

       }
       catch (Exception e)
       {
           System.out.println(e.getMessage());
       }


   }

    /**
     * 报表样式缓存
     * @param key
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
   public  List<resport<axinfo>> getgzreport(String key) throws NoSuchFieldException, IllegalAccessException {
       return  redisUtil.getlist(key);
   }
    /**
     * 动态活动凭证设置
     * @param key
     * @param vmname
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
    
    */
    public List<vouchtoglmodel> getvouchmanagerlist(String key,String vmname) throws NoSuchFieldException, IllegalAccessException
    {
        List<vouchtoglmodel> vouchglmodelList=this.redisUtil.getlist(key);
        if(vmname==null||vmname.equals(""))
        {
            return vouchglmodelList;
        }
        else
        {
            List<vouchtoglmodel> vsmodelresult=new ArrayList<vouchtoglmodel>();
            for (vouchtoglmodel iterable_element : vouchglmodelList) {
            if(iterable_element.getVouchmanager().getVmname().contains(vmname))
            {
              vsmodelresult.add(iterable_element);
            }
        }
            return vsmodelresult;
        }   
    }
    public List<Gl_Accvouch> dymicglvouchs(ufdatabasebasic info, vouchcontioan vouch)
    {
        return  this.gl_vouchmannager.dymamicvouchs(info,vouch);

    }


}
