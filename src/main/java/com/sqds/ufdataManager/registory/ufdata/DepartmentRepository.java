package com.sqds.ufdataManager.registory.ufdata;

import com.sqds.ufdataManager.model.ufdata.Department;
import com.sqds.ufdataManager.registory.DataDml.ufdatabasebasic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository("ufdatadepartmentRepository")
public interface DepartmentRepository extends JpaRepository<Department, String> {

    /**动态查询部门信息
     *
     */

    @Query(value = "select d from  Department  d where d.cDepCode = ?1 or ?1 is null or ?1=''")
    List<Department> getpartment(String depcpde, ufdatabasebasic info);
}
