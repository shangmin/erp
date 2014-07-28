package com.tiger.erp.common.jdbc;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

import org.apache.commons.beanutils.PropertyUtils;

import com.tiger.erp.util.H2Utils;

public abstract class JDBCTemplate {

    private String sql;
    
    private Object param;
    
    public JDBCTemplate(String sql) {
        this.sql = sql ;
    }
    
    public JDBCTemplate(String sql,Object param) {
        this.sql = sql ;
        this.param = param;
    }

    public Object execute() throws JDBCException {
        Connection conn = H2Utils.getConnection();
        try {
            ParsedSql parsedSql  = NamedParameterUtils.parseSqlStatement(sql);
            String sql2Use = NamedParameterUtils.substituteNamedParameters(parsedSql, new MapSqlParameterSource());
            PreparedStatement ps = conn. prepareStatement(sql2Use);
            int i = 1;
            for(String param: parsedSql.getParameterNames()){
                Object value = resolveParameterValue(param, this.param);
                setValue(ps,i++,value);
            }
            Object obj = execute(ps);
            conn.commit();
            H2Utils.closeConnection(conn);
            return obj;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                System.err.println(e1.getMessage());
            }
            throw new JDBCException(e.getMessage(), e);
        }
    }

    public abstract Object execute(PreparedStatement ps ) throws SQLException,JDBCException;
    
    private Object resolveParameterValue(String property, Object object) throws JDBCException{
        try {
            return PropertyUtils.getProperty(object, property);
        } catch (IllegalAccessException e) {
            throw new JDBCException(e);
        } catch (InvocationTargetException e) {
            throw new JDBCException(e);
        } catch (NoSuchMethodException e) {
            throw new JDBCException(e);
        }
    }
    private void setValue(PreparedStatement ps, int i, Object value) throws SQLException {
        if(value instanceof String || value instanceof Character) {
            ps.setString(i, value.toString());
        }else if (value instanceof BigDecimal) {
            ps.setBigDecimal(i, (BigDecimal) value);
        }if (value instanceof java.util.Date) {
            if (value instanceof java.sql.Date) {
                ps.setDate(i, (java.sql.Date) value);
            }else {
                ps.setDate(i, new java.sql.Date(((java.util.Date)value).getTime()));
            }
        }else if (value instanceof Calendar) {
            Calendar cal = (Calendar) value;
            ps.setDate(i, new java.sql.Date(cal.getTime().getTime()), cal);
        } else if (value instanceof java.sql.Timestamp) {
                ps.setTimestamp(i, (java.sql.Timestamp) value);
         }else {
             ps.setObject(i, value);
         }
    }

}
