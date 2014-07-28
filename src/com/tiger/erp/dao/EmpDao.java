package com.tiger.erp.dao;

import java.util.List;
import java.util.Vector;

import com.tiger.erp.common.jdbc.BaseDao;
import com.tiger.erp.po.Emp;


public interface EmpDao extends BaseDao<Emp, Integer>{
    
    Vector<Vector>  getEmp(Object criteria,int page,int pageSize);
    
    List<?>  getEmp(Object criteria);

    int countEmp(Object criteria);
    
    String[] getDept();
    
}
