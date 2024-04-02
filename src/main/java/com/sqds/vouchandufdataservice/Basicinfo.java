package com.sqds.vouchandufdataservice;

import com.sqds.comutil.RedisUtil;
import com.sqds.ufdataManager.model.ufdata.code;
import com.sqds.ufdataManager.model.ufdata.person;
import com.sqds.ufdataManager.registory.DataDml.Gl_AccVouchRepositoryHelp;
import com.sqds.ufdataManager.registory.ufdata.Gl_AccVouchRepository;
import com.sqds.ufdataManager.registory.ufdata.personRepository;
import com.sqds.vouchdatamanager.model.vouchmanager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import  com.sqds.vouchdatamanager.registroy.vouchmanagerRepository;
import  com.sqds.ufdataManager.registory.DataDml.ufdatabasebasic;

import java.util.List;
import java.util.Map;

@Service
public class Basicinfo {
    @Autowired
    private  vouchmanagerRepository vouchmanagerRepository;
    @Autowired
    private Gl_AccVouchRepository codemanager;
    @Autowired
    private personRepository presonmanager;
    @Autowired
    RedisUtil redis;


    /**
     * 东塔查询账套凭证模板设置
     * @param year
     * @param ufcode
     * @return
     */
    public List<vouchmanager> getvouchmanangerlist(int year,String ufcode)
    {
        return  this.vouchmanagerRepository.getvouchmanagerByufcode(ufcode,String.valueOf(year));

    }

    /**
     * 动态查询科目
     * @param info
     * @param ufcode
     * @return
     */
    public  List<code>  codelist(ufdatabasebasic info, String ufcode)
    {
         return codemanager.coderesult(info,ufcode);

    }

    /**
     * 根据科目动态查询辅助账信息列表
     * @param info
     * @param ufcode
     * @return
     */
    public Map<String,List<Gl_AccVouchRepositoryHelp.axinfo>> getaxinfo(ufdatabasebasic info, String ufcode)
    {

        return  codemanager.getaxinfofromCode(info,ufcode);
    }

    public List<person> getpersonlist(String person, ufdatabasebasic info)
    {
        //从缓存中取数，若缓存中没有数值，则设置人员缓存
            
            List<person> personlist = (List<person>) this.presonmanager.findallByPerson_NameOrAndCPsn_Num(person,info);
           /* if(person==null || person.isEmpty())
            {
                this.redis.set("ufperson",personlist);
            }*/
            return  personlist;

    }


}
