package com.sqds.vouchdatamanager.registroy;

import com.sqds.ufdataManager.model.ufdata.person;
import com.sqds.vouchdatamanager.Help.vouchcontion;
import com.sqds.vouchdatamanager.model.newsvouchs;
import com.sqds.vouchdatamanager.model.newsVouchsAllocations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 发票订单模板操作接口
 */

public interface newsvouchsmanager
{
  
    List<newsvouchs> getvouchde(vouchcontion vouchcontion,String datasource);
    List<newsvouchs> getvouchallocation(vouchcontion vouchcontion,String dataource);
    Integer allocation(List<newsVouchsAllocations> newsVouchAllocations,String cpsn_num,Integer count,String datasource);
    List<newsvouchs> allocationend(List<newsvouchs> needall,String datasource);
    List<newsvouchs> fendanlist(vouchcontion vouchcontion, String datasource);
    String fendan(newsvouchs needall, Map<String,Integer > needperson, String datasource);
    List<newsvouchs> sumyejibyperson(vouchcontion vouchcontion, String datasource);
    Map sumtext(LocalDateTime begindate, LocalDateTime enddate,String database);

    Map<String,Object> sumtext(LocalDateTime begindate, LocalDateTime enddate, String maname, String database);

    List<newsvouchs> vouchsum(vouchcontion vouchcontion, String database);
    int addbannode(List<newsvouchs> node,String cdigest,String database);


    person getpersobyname(String personname, String ufpzhangtao,String databse);

    Integer Insertorupdatevouchs(List<newsvouchs> updatevouchs, String year);

    Integer cancle(List<newsvouchs> needcanclevouchs,String year,boolean isshoukuan);
}
