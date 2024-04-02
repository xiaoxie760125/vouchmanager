package com.sqds.Reflect;

import org.springframework.cglib.beans.BeanGenerator;
import org.springframework.cglib.beans.BeanMap;

import java.util.Map;

public class DynamicBean {
    private  Object target;
    private BeanMap beanMap;
    public DynamicBean(Class superclass, Map<String,Class> propertyMap)
    {
                this.target=generateBean(superclass,propertyMap);
        this.beanMap=BeanMap.create(target);
    }
    public  void  setValue(String propertyName,Object value)
    {
                beanMap.put(propertyName,value);
    }
    public Object  getValue(String propertyName)
    {
        return beanMap.get(propertyName);
    }
    public Object getTarget()
    {
        return this.target;
    }
    private Object generateBean(Class superclass, Map<String, Class> propertyMap) {
        BeanGenerator  generator=new BeanGenerator();
        if(null!=superclass)
        {
            generator.setSuperclass(superclass);
        }
        BeanGenerator.addProperties(generator,propertyMap);

        return generator.create();
    }
}
