package com.tiger.erp.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.tiger.erp.common.jdbc.BaseDaoImpl;
import com.tiger.erp.dao.DaoFactory;
import com.tiger.erp.dao.EmpDao;
import com.tiger.erp.po.Emp;
import com.tiger.erp.util.StringUtils;

public class EmpDaoImpl extends BaseDaoImpl<Emp, Integer> implements EmpDao {

    @Override
    public int countEmp(Object criteria) {
        String sql = "SELECT  count(id) FROM  EMP";
        return querySqlCount(sql, criteria);
    }
    
    @Override
    public Vector<Vector> getEmp(Object criteria,int page, int pageSize) {
        String sql = "SELECT EMP.ID,STAFF_NO,STAFF_NAME,PHONE,STATUS,ENTRY_DATE,DEPT_NAME  FROM EMP LEFT OUTER JOIN DEPT ON EMP.DEPT_ID = DEPT.ID WHERE 1=1 ";
        Emp emp = (Emp)criteria;
        if(StringUtils.isNotEmptyString(emp.getStaffNo())) {
            sql += " and STAFF_NO like :staffNo";
        }
        if(StringUtils.isNotEmptyString(emp.getStaffName())) {
            sql += " and STAFF_NAME like :staffName";
        }
        if(StringUtils.isNotEmptyString(emp.getStatus())) {
            sql += " and STATUS = :status";
        }
        if(emp.getDeptId() != null && emp.getDeptId() != 0) {
            sql += " and DEPT_ID = :deptId";
        }
        if(emp.getEntryDate() != null) {
            sql += " and ENTRY_DATE >= :entryDate";
        }

        return querySqlForVector(sql, criteria, page, pageSize);
    }

    @Override
    public List<?> getEmp(Object criteria) {
        String sql = "SELECT STAFF_NO,STAFF_NAME,PHONE,STATUS,ENTRY_DATE,DEPT_NAME  FROM EMP LEFT OUTER JOIN DEPT ON EMP.DEPT_ID = DEPT.ID WHERE 1=1 ";
        Emp emp = (Emp)criteria;
        if(StringUtils.isNotEmptyString(emp.getStaffNo())) {
            sql += " and STAFF_NO like :staffNo";
        }
        if(StringUtils.isNotEmptyString(emp.getStaffName())) {
            sql += " and STAFF_NAME like :staffName";
        }
        if(StringUtils.isNotEmptyString(emp.getStatus())) {
            sql += " and STATUS = :status";
        }
        if(emp.getDeptId() != null && emp.getDeptId() != 0) {
            sql += " and DEPT_ID = :deptId";
        }
        if(emp.getEntryDate() != null) {
            sql += " and ENTRY_DATE >= :entryDate";
        }
        List<Object[]> list = (List<Object[]>) querySqlForList(sql, criteria);
        List<Emp>  result = new ArrayList<Emp>();
        if(list != null && !list.isEmpty()) {
            for (Object[] objs : list) {
                Emp e = new Emp();
                e.setStaffNo(objs[0].toString());
                e.setStaffName(objs[1].toString());
                e.setPhone(objs[2].toString());
                e.setStatus(objs[3].toString());
                e.setEntryDate((Date) objs[4]);
                result.add(e);
            }
        }
        return result;
    }

    @Override
    public String[] getDept() {
        String sql = "SELECT  DEPT_NAME  FROM  DEPT ORDER BY ID";
        List<String> list = (List<String>) querySqlForList(sql, null);
        String[] result = null;
        if(list != null && !list.isEmpty()) {
            result = new String[list.size() + 1];
            result[0] = "";
            for (int i = 0; i < list.size(); i++) {
                result[i+1] = list.get(i);
            }
        }
        return result;
    }
    public static void main(String[] args) {
        EmpDao dao = DaoFactory.getInstance().getEmpDao();
        dao.getEmp(new Emp(), 1, 10);
        dao.getEmp(new Emp(), 1, 10);
    }
}
