package com.sqds.vouchdatamanager.registroy;

import com.sqds.vouchdatamanager.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository  extends JpaRepository<Department,String> {
}
