package com.sqds.ufdataManager.registory.ufdata;

import com.sqds.ufdataManager.model.ufdata.Gl_Accvouch;
import com.sqds.ufdataManager.registory.DataDml.ufdatabasebasic;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface Gl_vouchClientRepository {

    /**
     * 凭证列表
     * @param begindperiod
     * @param endperiod
     * @return
     */
  List<Gl_Accvouch> findvouchs(Integer begindperiod, Integer endperiod, ufdatabasebasic info,int ino_id);

  @Cacheable(key = "#info.getZhangtaohao()+'-'+#begindate.getMonthValue()+#enddate.getMonthValue()+'code'",cacheNames ="codenum")
  Map<Integer,Integer> getinum(LocalDateTime begindate, LocalDateTime enddate, ufdatabasebasic info);

  List<Gl_Accvouch> findvouchs(Integer period, ufdatabasebasic info);

    /**
     * 结账
     * @param period
     * @return
     */
    Integer bookvouchs(Integer period ,ufdatabasebasic info);

    /**
     * 用友报表公式
     * @param fromul
     * @param vc
     * @return
     */
    Double  UFFormula(fromul fromul,vouchcontioan vc,ufdatabasebasic info);


}
