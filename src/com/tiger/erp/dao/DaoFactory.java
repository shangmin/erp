package com.tiger.erp.dao;

import com.tiger.erp.dao.impl.EmpDaoImpl;
import com.tiger.erp.dao.impl.SalaryDaoImpl;
import com.tiger.erp.dao.impl.TestDaoImpl;

public class DaoFactory {

    private static DaoFactory daoFactory;

    private DaoFactory() {

    }

    public static DaoFactory getInstance() {
        if (daoFactory == null) {
            daoFactory = new DaoFactory();
        }
        return daoFactory;
    }
    public EmpDao getEmpDao() {
        return new EmpDaoImpl();
    }
    public SalaryDao getSalaryDao() {
        return new SalaryDaoImpl();
    }
    public TestDao TestDao() {
        return new TestDaoImpl();
    }
}
