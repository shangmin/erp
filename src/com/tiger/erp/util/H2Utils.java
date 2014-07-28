package com.tiger.erp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.h2.jdbcx.JdbcConnectionPool;

import com.tiger.erp.dao.DaoFactory;
import com.tiger.erp.dao.TestDao;
import com.tiger.erp.po.Test;


public class H2Utils {

    static JdbcConnectionPool cp = null;
    static {
        try {
            Class.forName("org.h2.Driver");
           cp = JdbcConnectionPool.create("jdbc:h2:D:\\cis2_workspace\\erp\\db\\erp", "sa", "");
           cp.setMaxConnections(10);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        Connection conn = null;
        try {
             conn = cp.getConnection(); 
           // conn = DriverManager.getConnection("jdbc:h2:D:\\cis2_workspace\\erp\\db\\erp", "sa", "");
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static Statement getStatement() {
        try {
            Statement stmt = getConnection().createStatement();
            return stmt;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PreparedStatement getPreparedStatement(String sql) {
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            return ps;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {

        
        Test  t = new Test(); 
        t.setId(5);
        t.setName("I am qw");
        TestDao testDao = DaoFactory.getInstance().TestDao();
        // testDao.save(t);
         t = testDao.find(3);
         System.out.println(t.getId() + ":" + t.getName());
       // t = testDao.update(t);
       // testDao.delete(5);
//                  for(Test tt : testDao.getAll()) {
//                  System.out.println(tt.getId() + ":" + tt.getName());
//                    }
//    System.out.println(testDao.getMaxPK());
//        Statement stmt = getStatement();
//        ResultSet rs = stmt.executeQuery("SELECT * FROM TEST ");
//        while (rs.next()) {
//            System.out.println(rs.getInt("ID") + "," + rs.getString("NAME"));
//        }
        String sql = "select * from test where id = :id  and name = :name ";
        sql = "select  count(*)  from test where id = :id  and name = :name ";
        sql = "update  test set name = :name where id = :id ";
        Map criteria = new HashMap();
        criteria.put("id", 3);
        criteria.put("name", "Wo who?");
        t.setId(3);
        t.setName("Wo who?");
        List<Object[]>  result = (List<Object[]>) testDao.querySqlForList(sql, t,4,2);
        for (Object[] ojbs: result) {
            System.out.println(ojbs[0]);
            System.out.println(ojbs[1]);
        }
       // System.out.println(testDao.executeSqlUpdate(sql, criteria));
       
    }
}
