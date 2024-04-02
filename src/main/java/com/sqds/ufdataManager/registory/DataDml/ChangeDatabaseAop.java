package com.sqds.ufdataManager.registory.DataDml;

import com.sqds.ufdataManager.registory.ufsystem.Ua_periodRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Aspect
@Component
public class ChangeDatabaseAop {
    @Autowired
    @Qualifier("ufdataDataSource")
    DataSource ufdatadatasource;
    @Autowired
    Ua_periodRepository periodmanager;

    @Pointcut(value ="execution(* com.sqds.ufdataManager.registory.ufdata.*.*(..))")
    private  void changedatabase()
    {

    }
    @Pointcut(value ="execution(* com.sqds.ufdataManager.registory.ufdata.*.*(..)) && args(..,info)")
    private  void changedatabaseafterpara(ufdatabasebasic info)
    {

    }
    @Before("changedatabase()")
    public  void  changeufdatabase(JoinPoint point) throws SQLException {

        ufdatabasebasic info=getPersmterByName(point,"info",ufdatabasebasic.class);
        int year=periodmanager.findMaxPeriodByCAcc_id(info.getZhangtaohao());

        //¿çÄê²éÑ¯
        if(info.getYear()==0 || info.getYear()>year)
        {

            info.setYear(year);
        }
        String pathern="Ufdata_(?<account>\\d{3})_(?<year>\\d{4,6})";
        Pattern ufaccount=Pattern.compile(pathern);
        DriverManagerDataSource datasourceConfig=(DriverManagerDataSource)ufdatadatasource;
        String url=datasourceConfig.getUrl();
        Matcher urmath=ufaccount.matcher(url);
        if(urmath.find())
        {
            //if(!urmath.group("account").equals(info.getZhangtaohao()) || !(Integer.parseInt(urmath.group("year"))==info.getYear())) {
                datasourceConfig.setUrl(url.replaceAll(pathern, "Ufdata_" + info.getZhangtaohao() + "_" + Integer.toString(info.getYear())));
            //}
        }



    }

    public  static  <T> T getPersmterByName(JoinPoint joinPoint,String name,Class<T> clazz)
    {
        Object[] arges=joinPoint.getArgs();
        Optional<Object> result= Arrays.asList(arges).stream()
                .filter((s)-> {
                    return s.getClass().equals(clazz);
                }).findFirst();

        return (T)result.get();
    }
}
