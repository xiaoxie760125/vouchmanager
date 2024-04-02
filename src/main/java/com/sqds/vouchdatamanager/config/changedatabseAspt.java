package com.sqds.vouchdatamanager.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Aspect
@Component
public class changedatabseAspt {

    @Autowired
    @Qualifier("vouchdatasource")
    private DataSource dataSource;


    @Pointcut(value = "execution(* com.sqds.vouchdatamanager.registroy.*.*(..)) && args(..,datasource)")
    public void   changedatabase(String datasource)
    {
    }
    @Pointcut(value ="execution(* com.sqds.vouchdatamanager.registroy.newsvouchsRepository.*(..))")
    public  void  aspect()
    {

    }

    @Before("changedatabase(datasource)")
    public void   Getdatabase(JoinPoint point,String datasource)
    {

        String url="databaseName=newsvouchs(?<year>(\\d{4}|[\u4e00-\u9fa5]{1,9}))";
       /* for (Object P:point.getArgs())
        {
            System.out.println(P);
        }*/
        Pattern pattern=Pattern.compile(url);
        DriverManagerDataSource datas=(DriverManagerDataSource) dataSource;
        String urls=datas.getUrl();
        Matcher matcher=pattern.matcher(urls);
        if(matcher.find() && !matcher.group("year").equals(datasource))
        {
             urls=urls.replace(matcher.group("year"),datasource);
             datas.setUrl(urls);
            System.out.println("this is a aspect");
        }
        else
        {
            System.out.println("this is a aspect");
        }
    }
   /* @Before("aspect()")
    public void   Getdatabase(JoinPoint point)
    {
        var param=point.getArgs();
        for (Object object:param)
        {
            if(object instanceof String)
            {
                System.out.println(object);
            }
        }


         System.out.println("this is a aspect");

    }*/
}
