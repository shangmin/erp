package com.tiger.erp.dao.impl;

import java.util.List;
import java.util.Vector;

import com.tiger.erp.common.jdbc.BaseDaoImpl;
import com.tiger.erp.common.jdbc.JDBCException;
import com.tiger.erp.dao.DaoFactory;
import com.tiger.erp.dao.SalaryDao;
import com.tiger.erp.po.SalaryItem;

public class SalaryDaoImpl extends BaseDaoImpl<SalaryItem, Integer> implements SalaryDao {

    @Override
    public Vector<Vector> getSalaryItem() throws JDBCException {
        List<SalaryItem> list = null;
        list = getAll();
        Vector result = new Vector();
        for (SalaryItem salaryItem : list) {
            Vector vector = new Vector();
            vector.add(salaryItem.getId());
            vector.add(salaryItem.getItem());
            vector.add(salaryItem.getCharge());
            result.add(vector);
        }
        return result;
    }

    @Override
    public void saveSalaryItem(Vector<Vector> data) {

    }


    @Override
    public Vector<Vector> getSalaryItem(int page, int pageSize) {
        String sql = "SELECT  *  FROM  SALARY_ITEM";
        return querySqlForVector(sql, null, page, pageSize);
    }

    @Override
    public int countSalaryItem() {
        String sql = "SELECT  count(id) FROM  SALARY_ITEM";
        return querySqlCount(sql, null);
    }

}
