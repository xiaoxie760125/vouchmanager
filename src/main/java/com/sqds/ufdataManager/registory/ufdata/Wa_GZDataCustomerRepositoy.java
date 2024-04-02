package com.sqds.ufdataManager.registory.ufdata;

import com.sqds.springjwtstudy.controller.responsemodel.GzUfModel;
import com.sqds.ufdataManager.model.ufdata.Department;
import com.sqds.ufdataManager.model.ufdata.Gl_Accvouch;
import com.sqds.ufdataManager.registory.DataDml.ufdatabasebasic;
import com.sqds.vouchandufdataservice.axinfo;
import com.sqds.vouchandufdataservice.resport;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.List;

public interface Wa_GZDataCustomerRepositoy {
    String  GetSqlService(vouchcontioan c,String[] columnnames,ufdatabasebasic zhangtaohao);

    /**
     * ���ݹ����������ɻ��ƾ֤
     * @param c
     * @param gzUfModel
     * @param zhangtaohao
     * @return
     */
    List<Gl_Accvouch> GetGzUfDalModel(vouchcontioan c, GzUfModel gzUfModel, ufdatabasebasic zhangtaohao);

    <T> List<T> FindGzinfoIsum(vouchcontioan c, String[] columnames,ufdatabasebasic info) throws InvocationTargetException, IllegalAccessException, InstantiationException;

    /**
     * �������ݶ�̬��ѯ
     * @param c
     * @param info
     * @return
     * @param <T>
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    <T> List<T> FindGzinfo(vouchcontioan c,ufdatabasebasic info) throws InvocationTargetException, IllegalAccessException, InstantiationException;
     <T> List<T> FindGzinfo(vouchcontioan c,String[] columnames,ufdatabasebasic info)  throws InvocationTargetException, IllegalAccessException;
    /**
     * ���ʵı�Ͷ����
     *
     * @param columnames
     * @param isorg
     * @param info
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    List<axinfo> getcolums (String[] columnames, boolean isorg, ufdatabasebasic info) throws InvocationTargetException, IllegalAccessException;


  vouchcontioan insertgzdatafromExcelFile(File file, ufdatabasebasic info);
 <T>  Workbook  getworkbook(List<T> gzlist, resport<List<LinkedHashMap<String,String>>> columnnames, ufdatabasebasic info) throws NoSuchFieldException, IllegalAccessException;
 List<Department> getdeptment(String code,ufdatabasebasic info);



    List<String> computerFormula(vouchcontioan vc, ufdatabasebasic info);
}
