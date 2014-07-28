package com.tiger.erp.dao;

import java.util.Vector;

import com.tiger.erp.common.jdbc.BaseDao;
import com.tiger.erp.common.jdbc.JDBCException;
import com.tiger.erp.po.SalaryItem;

public interface SalaryDao extends BaseDao<SalaryItem, Integer>{
    
    Vector<Vector>  getSalaryItem() throws JDBCException;
     int  countSalaryItem();
    Vector<Vector>  getSalaryItem(int page,int pageSize);
    void saveSalaryItem(Vector<Vector>  data) ;
}
