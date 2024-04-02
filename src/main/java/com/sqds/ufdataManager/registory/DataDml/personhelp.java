package com.sqds.ufdataManager.registory.DataDml;

import com.sqds.ufdataManager.model.ufdata.person;
import com.sqds.ufdataManager.registory.ufdata.personRepository;
import com.sqds.ufdataManager.registory.ufsystem.Ua_periodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ComponentScan({"com.sqds.ufdataManager.registory.DataDml","com.sqds.ufdataManager.registory.DataDml","com.sqds.ufdataManager.registory.ufsystem"})
public class personhelp {

    @Autowired
    private personRepository ufperson;
    @Autowired
    @Qualifier("ufdatainfo")
    ufdatabasebasic info;
    @Autowired
    Ua_periodRepository periodRepository;


    /**
     * 取得人员列表，并从缓存输入
     * @param zhangtaohao
     * @return
     */
    @Cacheable(cacheNames ="ufpeson",key = "#zhangtaohao+'ufperson'")
    public List<person> GetPersonFromPsn_num( String zhangtaohao) {
        if(info==null)
        {
            info=new ufdatabasebasic();



        }
        if (info.getZhangtaohao()==null || !info.getZhangtaohao().equals(zhangtaohao)) {
            this.info.setZhangtaohao(zhangtaohao);
            int maxperiod = this.periodRepository.findMaxPeriodByCAcc_id(zhangtaohao);
            info.setYear(maxperiod);
        }



        return this.ufperson.findallByPerson_NameOrAndCPsn_Num("", info);

    }

    public  person getpersonbyaccoubt(String bankaccount,ufdatabasebasic info)
    {
        return  ufperson.findFirstByCPsnAccount(bankaccount,info);
    }
}
