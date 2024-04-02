package com.sqds.vouchdatamanager.Help;

import com.sqds.ufdataManager.model.ufdata.Gl_Accvouch;
import com.sqds.ufdataManager.registory.DataDml.ufdatabasebasic;
import com.sqds.vouchandufdataservice.axinfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("codeutil")
public class CodeUtil {
    @Autowired
    com.sqds.ufdataManager.registory.ufdata.codeRepository codeRepository;

    @Autowired
    com.sqds.ufdataManager.registory.ufdata.personRepository personRepository;

    public  axinfo setcodeaxinfo(Gl_Accvouch gl, Map<String,axinfo> info, ufdatabasebasic uainfo)
    {
        return  setcodeaxinfo(gl,gl.getCcode(),info,uainfo);
    }
    public  String getzengezhishui(boolean isjinxiang,ufdatabasebasic info)
    {
        String code=isjinxiang?"进项":"销项";
        try {
            var result=this.codeRepository.findAllByCcode(code, info);
            return this.codeRepository.findAllByCcode(code, info).get(0).getCcode();
        }
        catch (Exception ex)
        {
            return  "";
        }

    }


    public axinfo setcodeaxinfo(Gl_Accvouch gl, String code, Map<String,axinfo> info, ufdatabasebasic uainfo)
    {
        if(info!=null) {
            com.sqds.ufdataManager.model.ufdata.code ncode = this.codeRepository.findFirstByCcode(code, uainfo);

            if (ncode.getBperson()) {
                var person = this.personRepository.findFirstByCPersonCode(info.get("person").getValue(), uainfo);
                gl.setCperson_id(person.getCPsn_Num());
                gl.setCdept_id(person.getCDept_num());
                return info.get("person");

            }
            if (ncode.getBsup()) {
                gl.setCsup_id(info.get("sup").getValue());
                return info.get("sup");
            }
            if (ncode.getBcus()) {
                gl.setCcus_id(info.get("cus").getValue());
                return info.get("cus");
            }
            if (ncode.getBitem()) {
                gl.setCitem_id(info.get("item").getValue());
                gl.setCitem_class(ncode.getCass_item());
                return info.get("item");
            }
            if (ncode.getBdept()) {
                gl.setCdept_id(info.get("dep").getValue());
                return info.get("dep");
            }
        }
        return  null;
    }

}


