package com.sqds.vouchandufdataservice;

import com.sqds.ufdataManager.model.ufdata.Gl_Accvouch;
import com.sqds.ufdataManager.model.ufdata.code;
import com.sqds.ufdataManager.registory.DataDml.ufdatabasebasic;
import com.sqds.ufdataManager.registory.ufdata.codeRepository;

import java.util.List;
import java.util.Map;

public interface vouchtoufdata<T>{

 abstract  List<Gl_Accvouch> getGl_Accvouch(List<T> newsvouchs, vouchtoglmodel vouchmodelpara);


 /**
  * 给凭证填写辅助核算项目
  * @param gl_Accvouch
  * @param axinfo
  */
 default void   getGl_Accvouchaxinfo(List<Gl_Accvouch> gl_Accvouch ,
                                     Map<String,axinfo> axinfo)
 {
  if(axinfo!=null)
  {
   //添加辅助核算项
   for(Gl_Accvouch gl_needup:gl_Accvouch)
   {
      axinfo.forEach((s,value)->{
       switch (s)
       {
        case "dep":
         gl_needup.setCdept_id(value.value);
         break;
        case "person":
         gl_needup.setCperson_id(value.value);
         gl_needup.setCdept_id(value.title);
         break;
        case "item":
         gl_needup.setCitem_id(value.value);
         break;
        case  "cus":
         gl_needup.setCcus_id(value.value);
         break;
        case "sup":
         gl_needup.setCsup_id(value.value);
         break;

       }
      });


   }
  }

 }

 /**
  * 判断凭证是否有辅助核算项目
  * @param codelist
  * @param ccode
  * @return
  */
 default boolean isaxsun(codeRepository codelist, String ccode,ufdatabasebasic info)
 {
   code cn= codelist.findFirstByCcode(ccode,info);
   return  cn.getBdept() ||cn.getBcus() || cn.getBperson() ||cn.getBitem() || cn.getBsup();

 }
}
