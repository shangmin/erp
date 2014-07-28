package com.tiger.erp.common.jdbc;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

public interface BaseDao<T, PK extends Serializable> {

    public T save(T t) throws JDBCException;

    public T find(PK pk) throws JDBCException;

    public T update(T t) throws JDBCException;

    public void delete(PK pk) throws JDBCException;

    public List<T> getAll() throws JDBCException;

    public PK getMaxPK() throws JDBCException;

    public List<?> querySqlForList(String sql, Object criteria);

    public Vector<Vector> querySqlForVector(String sql, Object criteria);

    public List<?> querySqlForList(String sql, Object criteria, int page, int pageSize);

    public Vector<Vector> querySqlForVector(String sql, Object criteria, int page, int pageSize);

    public Integer querySqlCount(String sql, Object criteria);

    public int executeSqlUpdate(String sql, Object criteria) throws JDBCException;
}
