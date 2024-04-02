package com.sqds.Reflect;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtilsBean;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ReflectUtil {
     public static Object getObject(Object dest, Map<String,Object> newvalueMap) throws InvocationTargetException, IllegalAccessException {
        PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
        PropertyDescriptor[] descriptors=
                propertyUtilsBean.getPropertyDescriptors(dest);


        Map<String,Class> Oldkeymap=new
                HashMap<String,Class>();
        for(PropertyDescriptor descriptor:descriptors)
        {
            if(!"class".equals(descriptor.getName()))
            {
                Oldkeymap.put(descriptor.getName(),descriptor.getPropertyType());
                newvalueMap.put(descriptor.getName(),descriptor.getReadMethod().invoke(dest));
            }
        }
        newvalueMap.forEach((key,value)->Oldkeymap.put(key,value==null?String.class:value.getClass()));

         DynamicBean bean=new DynamicBean(dest.getClass(),Oldkeymap);

         newvalueMap.forEach((k,v)->{
             try {

                 bean.setValue(k,v);
             }
             catch (Exception e)
             {
                 log.error(" CUOSWU",e);
             }
         });
        return  bean.getTarget();

    }


}
