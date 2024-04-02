package com.sqds.ufdataManager.registory.DataDml;

import com.sqds.fileutil.pdfutil.PdfUtil;
import com.sqds.ufdataManager.model.ufdata.Gl_Accvouch;
import com.sqds.ufdataManager.registory.ufdata.*;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class GlvouchClient implements Gl_vouchClientRepository {
    @PersistenceUnit(unitName = "ufdata")
    EntityManagerFactory ufdatamanagerfactory;
    @Autowired
    codeRepository codeRep;
    @Autowired
    PdfUtil pdfutil;
    @Autowired
    Gl_AccVouchRepository glvouchservice;
    @Autowired
    personRepository persontool;

    @Override
    public  List<Gl_Accvouch> findvouchs(Integer begindperiod, Integer endperiod,ufdatabasebasic info,int ino_id) {


        EntityManager entityManager=ufdatamanagerfactory.createEntityManager();
        List<Gl_Accvouch>result=new LinkedList<>();
        try {
            TypedQuery<Gl_Accvouch> getvouchs = entityManager.createQuery("select gl from Gl_Accvouch  gl where iperiod between :begindperiod and :endperiod and ino_id=:ino_id  order by  iperiod,ino_id", Gl_Accvouch.class);

            getvouchs.setParameter("begindperiod", begindperiod);
            getvouchs.setParameter("endperiod", endperiod);
            getvouchs.setParameter("ino_id", ino_id);
             result= getvouchs.getResultList();

             for(Gl_Accvouch v:result)
             {
                 v=this.pdfutil.getaxinfo(info.getZhangtaohao(),v);
             }

        }
        catch (Exception e)
        {

            System.out.println(e.getMessage());
        }
        return  result;





    }
    @Override
    @Cacheable(key = "#info.getZhangtaohao()+#begindate.getYear()+'-'+#begindate.getMonthValue()+#enddate.getMonthValue()+'code'",cacheNames ="codenum")
    public Map<Integer,Integer> getinum(LocalDateTime begindate, LocalDateTime enddate, ufdatabasebasic info)
    {

       EntityManager codeentity=this.ufdatamanagerfactory.createEntityManager();
        Query selectcount=codeentity.createQuery("select  gl.iperiod,max(gl.ino_id) from Gl_Accvouch  gl where iperiod between :begindate and :enddate   group by iperiod order by  iperiod");

        selectcount.setParameter("begindate", begindate.getMonthValue());
        selectcount.setParameter("enddate", enddate.getMonthValue());

        List<Object[]> result=selectcount.getResultList();
        Map<Integer,Integer> inum=new HashMap<>();
        for(Object[] r:result)
        {

            inum.put(Integer.parseInt(r[0].toString()),Integer.parseInt(r[1].toString()));
        }
        return  inum;
    }
    @Override
    public List<Gl_Accvouch> findvouchs(Integer period, ufdatabasebasic info) {
        EntityManager entityManager=ufdatamanagerfactory.createEntityManager();

        return null;
    }

    @Override
    public Integer bookvouchs(Integer period,ufdatabasebasic info){
        return null;
    }

    @Override
    public Double UFFormula(fromul fromul, vouchcontioan vc,ufdatabasebasic info) {
        return null;
    }
}
