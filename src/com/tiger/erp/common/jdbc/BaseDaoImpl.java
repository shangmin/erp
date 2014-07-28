package com.tiger.erp.common.jdbc;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import com.tiger.erp.common.FuzzyQuery;
import com.tiger.erp.common.SystemConstant;
import com.tiger.erp.po.Test;
import com.tiger.erp.util.Assert;
import com.tiger.erp.util.DateUtil;
import com.tiger.erp.util.StringUtils;

public class BaseDaoImpl<T, PK extends Serializable> implements BaseDao<T, PK> {

    public static final String GET_METHOD_PREFIX = "get";

    public static final String SET_METHOD_PREFIX = "set";

    public static final Logger LOGGER = Logger.getLogger(BaseDaoImpl.class);

    Map<String, String> fieldColumn = new HashMap<String, String>();
    Map<String, Object> fieldValue = new HashMap<String, Object>();
    Map<String, Class> fieldType = new HashMap<String, Class>();
    List<String> fieldList = new ArrayList<String>();
    Id id = null;
    Field idField = null;
    Object idValue = null;
    Class clazz = getClazz();
    String tableName = null;

    public BaseDaoImpl() {
        super();
        Table table = (Table) clazz.getAnnotation(Table.class);
        tableName = table.name() == null ? clazz.getName().toUpperCase() : table.name().toUpperCase();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (id == null) {
                id = field.getAnnotation(Id.class);
                if (id != null) {
                    idField = field;
                }
            }
            Column column = field.getAnnotation(Column.class);
            String columnName = column.name() == null ? field.getName().toUpperCase() : column.name().toUpperCase();
            fieldList.add(field.getName());
            fieldColumn.put(field.getName(), columnName);
            fieldType.put(field.getName(), field.getType());
        }
    }

    @Override
    public T save(T t) throws JDBCException {
        String sql = buildSaveSql(t);
        System.out.println("[SQL:]" + sql.toString());
        new JDBCTemplate(sql) {
            @Override
            public Object execute(PreparedStatement ps) throws SQLException {
                ps.execute();
                return null;
            }
        }.execute();
        return t;
    }

    @Override
    public T find(final PK pk) throws JDBCException {
        T t = null;
        String sql = buildFindSql();
        t = (T) new JDBCTemplate(sql) {
            @Override
            public Object execute(PreparedStatement ps) throws SQLException, JDBCException {
                ps.setInt(1, (Integer) pk);
                ResultSet rs = ps.executeQuery();
                List list = buildResult(rs);
                if(list.isEmpty()) {
                    return null;
                } else {
                    return list.get(0);
                }
            }
        }.execute();
        return t;
    }

    @Override
    public T update(T t) throws JDBCException {
        String sql = buildUpdateSql(t);
        new JDBCTemplate(sql) {
            @Override
            public Object execute(PreparedStatement ps) throws SQLException, JDBCException {
                ps.execute();
                return null;
            }
        }.execute();
        return t;
    }

    @Override
    public void delete(final PK pk) throws JDBCException {
        String sql = buildDeleteSql();
        new JDBCTemplate(sql) {
            @Override
            public Object execute(PreparedStatement ps) throws SQLException, JDBCException {
                ps.setInt(1, (Integer) pk);
                ps.executeUpdate();
                return null;
            }
        }.execute();
    }

    @Override
    public List<T> getAll() throws JDBCException {
        String sql = buildGetAllSql();
        return (List<T>) new JDBCTemplate(sql) {
            @Override
            public Object execute(PreparedStatement ps) throws SQLException, JDBCException {
                ResultSet rs = ps.executeQuery();
                List<T> list = buildResult(rs);
                return list;
            }
        }.execute();
    }

    @Override
    public PK getMaxPK() throws JDBCException {
        String sql = buildPKSql();
        return (PK) new JDBCTemplate(sql) {
            @Override
            public Object execute(PreparedStatement ps) throws SQLException, JDBCException {
                ResultSet rs = ps.executeQuery();
                rs.next();
                return (PK) Integer.valueOf(rs.getInt(1) + 1);
            }
        }.execute();
    }

    @Override
    public List<?> querySqlForList(String sql, Object criteria) {
        criteria = handleFuzzyQueryCriteria(criteria);
        try {
            return (List<?>) new JDBCTemplate(sql, criteria) {
                @Override
                public Object execute(PreparedStatement ps) throws SQLException, JDBCException {
                    ResultSet rs = ps.executeQuery();
                    return buildResult2Object(rs);
                }
            }.execute();
        } catch (JDBCException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Vector<Vector> querySqlForVector(String sql, Object criteria) {
        criteria = handleFuzzyQueryCriteria(criteria);
        try {
            return (Vector<Vector>) new JDBCTemplate(sql, criteria) {
                @Override
                public Object execute(PreparedStatement ps) throws SQLException, JDBCException {
                    ResultSet rs = ps.executeQuery();
                    return buildResult2Vector(rs);
                }
            }.execute();
        } catch (JDBCException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<?> querySqlForList(String sql, Object criteria, int page, int pageSize) {
        List list = querySqlForList(sql, criteria);
        if (list == null || list.isEmpty()) {
            return null;
        } else {
            int startRow = getStartRow(page, pageSize);
            if (list.size() - startRow >= pageSize) {
                return list.subList(startRow, startRow + pageSize);
            } else {
                return list.subList(startRow, list.size());
            }
        }
    }

    @Override
    public Vector<Vector> querySqlForVector(String sql, Object criteria, int page, int pageSize) {
        Vector<Vector> vector = querySqlForVector(sql, criteria);
        if (vector == null || vector.isEmpty()) {
            return null;
        } else {
            int startRow = getStartRow(page, pageSize);
            if (vector.size() - startRow >= pageSize) {
                List<Vector> list = vector.subList(startRow, startRow + pageSize);
                return new Vector<Vector>(list);
            } else {
                List<Vector> list = vector.subList(startRow, vector.size());
                return new Vector<Vector>(list);
            }
        }
    }

    @Override
    public Integer querySqlCount(String sql, Object criteria) {
        criteria = handleFuzzyQueryCriteria(criteria);
        try {
            return (Integer) new JDBCTemplate(sql, criteria) {
                @Override
                public Object execute(PreparedStatement ps) throws SQLException, JDBCException {
                    ResultSet rs = ps.executeQuery();
                    rs.next();
                    return rs.getInt(1);
                }
            }.execute();
        } catch (JDBCException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int executeSqlUpdate(String sql, Object criteria) throws JDBCException {
        return (Integer) new JDBCTemplate(sql, criteria) {
            @Override
            public Object execute(PreparedStatement ps) throws SQLException, JDBCException {
                return ps.executeUpdate();
            }
        }.execute();
    }

    private String buildSaveSql(T t) throws JDBCException {
        putFieldValue(t);
        //  INSERT INTO table_name (列1, 列2,...) VALUES (值1, 值2,....)
        StringBuffer sql = new StringBuffer();
        sql.append(SQLKeyWord.INSERT);
        sql.append(SQLKeyWord.SPACE);
        sql.append(SQLKeyWord.INTO);
        sql.append(SQLKeyWord.SPACE);
        sql.append(tableName);
        sql.append(SQLKeyWord.SPACE);
        sql.append(SQLKeyWord.L_PARENTHESE);
        for (String fieldName : fieldList) {
            sql.append(fieldColumn.get(fieldName) + SQLKeyWord.COMMA);
        }
        sql = replaceEndString(sql, SQLKeyWord.R_PARENTHESE);
        sql.append(SQLKeyWord.SPACE);
        sql.append(SQLKeyWord.VALUES);
        sql.append(SQLKeyWord.SPACE);
        sql.append(SQLKeyWord.L_PARENTHESE);
        for (String fieldName : fieldList) {
            Object value = fieldValue.get(fieldName);
            if (String.class.equals(fieldType.get(fieldName))) {
                sql.append(SQLKeyWord.SINGLE_QUOTES);
                sql.append(StringUtils.toString(value));
                sql.append(SQLKeyWord.SINGLE_QUOTES);
                sql.append(SQLKeyWord.COMMA);
            } else if (Date.class.equals(fieldType.get(fieldName))) {
                if (value == null) {
                    sql.append(value);
                    sql.append(SQLKeyWord.COMMA);
                } else {
                    sql.append(SQLKeyWord.SINGLE_QUOTES);
                    sql.append(DateUtil.formatDate((Date) value, SystemConstant.DATE_CHS));
                    sql.append(SQLKeyWord.SINGLE_QUOTES);
                    sql.append(SQLKeyWord.COMMA);
                }
            } else {
                sql.append(StringUtils.toString(value));
                sql.append(SQLKeyWord.COMMA);
            }
        }
        sql = replaceEndString(sql, SQLKeyWord.R_PARENTHESE);
        return sql.toString();
    }

    private String buildFindSql() {
        //SELECT * FROM table where pk = ?
        StringBuffer sql = new StringBuffer();
        sql = selectAllFrom(sql);
        sql.append(tableName);
        sql.append(SQLKeyWord.SPACE);
        sql.append(SQLKeyWord.WHERE);
        sql.append(SQLKeyWord.SPACE);
        Column column = idField.getAnnotation(Column.class);
        sql.append(column.name());
        sql.append(SQLKeyWord.EQUAL);
        sql.append(SQLKeyWord.PLACEHOLDER);
        LOGGER.info("[SQL:]" + sql.toString());
        return sql.toString();
    }

    private List<T> buildResult(ResultSet rs) throws JDBCException {
        List<T> list = new ArrayList<T>();
        try {
            while (rs.next()) {
                Object object = clazz.newInstance();
                for (String fieldName : fieldList) {
                    Method method = clazz.getMethod(setMethodName(fieldName), fieldType.get(fieldName));
                    switchType(rs, method, object, fieldName);
                }
                list.add((T) object);
            }
            return list;
        } catch (SQLException e) {
            throw new JDBCException(e.getMessage(), e);
        } catch (InstantiationException e) {
            throw new JDBCException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new JDBCException(e.getMessage(), e);
        } catch (SecurityException e) {
            throw new JDBCException(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            throw new JDBCException(e.getMessage(), e);
        }
    }

    private List<?> buildResult2Object(ResultSet rs) throws JDBCException {
        List list = new ArrayList();
        try {
            while (rs.next()) {
                int count = rs.getMetaData().getColumnCount();
                int index = 0;
                if(count == 1) {
                    list.add(rs.getObject(1));
                }else {
                    Object[] objects = new Object[count];
                    while (index < count) {
                        objects[index] = rs.getObject(rs.getMetaData().getColumnLabel(++index));
                    }
                    list.add(objects);
                }
            }
            return list;
        } catch (SQLException e) {
            throw new JDBCException(e.getMessage(), e);
        } catch (SecurityException e) {
            throw new JDBCException(e.getMessage(), e);
        }
    }

    private Object buildResult2Vector(ResultSet rs) throws JDBCException {
        Vector<Vector> result = new Vector<Vector>();
        try {
            while (rs.next()) {
                int count = rs.getMetaData().getColumnCount();
                int index = 0;
                Vector vector = new Vector();
                while (index < count) {
                    vector.add(rs.getObject(rs.getMetaData().getColumnLabel(++index)));
                }
                result.add(vector);
            }
            return result;
        } catch (SQLException e) {
            throw new JDBCException(e.getMessage(), e);
        } catch (SecurityException e) {
            throw new JDBCException(e.getMessage(), e);
        }
    }

    private void switchType(ResultSet rs, Method method, Object object, String fieldName) throws JDBCException {
        Object value = null;
        try {
            if (Integer.class.equals(fieldType.get(fieldName))) {
                value = rs.getInt(fieldColumn.get(fieldName));
            } else if (String.class.equals(fieldType.get(fieldName))) {
                value = rs.getString(fieldColumn.get(fieldName));
            } else if (int.class.equals(fieldType.get(fieldName))) {
                value = rs.getInt(fieldColumn.get(fieldName));
            } else if (Double.class.equals(fieldType.get(fieldName))) {
                value = rs.getDouble((fieldColumn.get(fieldName)));
            } else if (double.class.equals(fieldType.get(fieldName))) {
                value = rs.getDouble(fieldColumn.get(fieldName));
            } else if (Boolean.class.equals(fieldType.get(fieldName))) {
                value = rs.getBoolean(fieldColumn.get(fieldName));
            } else if (boolean.class.equals(fieldType.get(fieldName))) {
                value = rs.getBoolean(fieldColumn.get(fieldName));
            } else if (Character.class.equals(fieldType.get(fieldName))) {
                value = rs.getString(fieldColumn.get(fieldName));
            } else if (char.class.equals(fieldType.get(fieldName))) {
                value = rs.getString(fieldColumn.get(fieldName));
            } else if (Byte.class.equals(fieldType.get(fieldName))) {
                value = rs.getByte(fieldColumn.get(fieldName));
            } else if (byte.class.equals(fieldType.get(fieldName))) {
                value = rs.getByte(fieldColumn.get(fieldName));
            } else if (Short.class.equals(fieldType.get(fieldName))) {
                value = rs.getShort(fieldColumn.get(fieldName));
            } else if (short.class.equals(fieldType.get(fieldName))) {
                value = rs.getShort(fieldColumn.get(fieldName));
            } else if (Long.class.equals(fieldType.get(fieldName))) {
                value = rs.getLong(fieldColumn.get(fieldName));
            } else if (long.class.equals(fieldType.get(fieldName))) {
                value = rs.getLong(fieldColumn.get(fieldName));
            } else if (Float.class.equals(fieldType.get(fieldName))) {
                value = rs.getFloat(fieldColumn.get(fieldName));
            } else if (float.class.equals(fieldType.get(fieldName))) {
                value = rs.getFloat(fieldColumn.get(fieldName));
            } else if (BigDecimal.class.equals(fieldType.get(fieldName))) {
                value = rs.getBigDecimal(fieldColumn.get(fieldName));
            } else if (Date.class.equals(fieldType.get(fieldName))) {
                value = rs.getDate(fieldColumn.get(fieldName));
            } else if (Timestamp.class.equals(fieldType.get(fieldName))) {
                value = rs.getTimestamp(fieldColumn.get(fieldName));
            } else if (Time.class.equals(fieldType.get(fieldName))) {
                value = rs.getTime(fieldColumn.get(fieldName));
            }
            method.invoke(object, value);
        } catch (SQLException e) {
            throw new JDBCException(e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new JDBCException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new JDBCException(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            throw new JDBCException(e.getMessage(), e);
        }
    }

    private String buildUpdateSql(T t) throws JDBCException {
        putFieldValue(t);
        //  UPDATE Person SET Address = 'Zhongshan 23', City = 'Nanjing' WHERE LastName = 'Wilson'
        StringBuffer sql = new StringBuffer();
        sql.append(SQLKeyWord.UPDATE);
        sql.append(SQLKeyWord.SPACE);
        sql.append(tableName);
        sql.append(SQLKeyWord.SPACE);
        sql.append(SQLKeyWord.SET);
        sql.append(SQLKeyWord.SPACE);
        for (String fieldName : fieldList) {
            if (fieldName.equals(idField.getName())) {
                idValue = fieldValue.get(fieldName);
                continue;
            }
            sql.append(fieldColumn.get(fieldName));
            sql.append(SQLKeyWord.EQUAL);
            Object value = fieldValue.get(fieldName);
       
            if (String.class.equals(value.getClass())) {
                sql.append(SQLKeyWord.SINGLE_QUOTES);
                sql.append(StringUtils.toString(value));
                sql.append(SQLKeyWord.SINGLE_QUOTES);
                sql.append(SQLKeyWord.COMMA);
            } else if (Date.class.equals(fieldType.get(fieldName))) {
                if (value == null) {
                    sql.append(value);
                    sql.append(SQLKeyWord.COMMA);
                } else {
                    sql.append(SQLKeyWord.SINGLE_QUOTES);
                    sql.append(DateUtil.formatDate((Date) value, SystemConstant.DATE_CHS));
                    sql.append(SQLKeyWord.SINGLE_QUOTES);
                    sql.append(SQLKeyWord.COMMA);
                }
            } else {
                sql.append(StringUtils.toString(value));
                sql.append(SQLKeyWord.COMMA);
            }
        }
        sql = replaceEndString(sql, SQLKeyWord.SPACE);
        sql.append(SQLKeyWord.WHERE);
        sql.append(SQLKeyWord.SPACE);
        sql.append(fieldColumn.get(idField.getName()));
        sql.append(SQLKeyWord.EQUAL);
        sql.append(idValue);
        System.out.println("[SQL:]" + sql.toString());
        return sql.toString();
    }

    private void putFieldValue(T t) throws JDBCException {
        try {
            for (String fieldName : fieldList) {
                Object object = null;
                if (fieldName.equals(idField.getName())) {
                    Method method = clazz.getMethod(getMethodName(fieldName));
                    object = method.invoke(t);
                    if(object == null) {
                        object = getMaxPK();
                    }
                    idValue = object;
                } else {
                    Method method = clazz.getMethod(getMethodName(fieldName));
                    object = method.invoke(t);
                }
                fieldValue.put(fieldName, object);
            }
        } catch (SecurityException e) {
            throw new JDBCException(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            throw new JDBCException(e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new JDBCException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new JDBCException(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            throw new JDBCException(e.getMessage(), e);
        }
    }

    private String buildDeleteSql() {
        StringBuffer sql = new StringBuffer();
        sql.append(SQLKeyWord.DELETE);
        sql.append(SQLKeyWord.SPACE);
        sql.append(SQLKeyWord.FROM);
        sql.append(SQLKeyWord.SPACE);
        sql.append(tableName);
        sql.append(SQLKeyWord.SPACE);
        sql.append(SQLKeyWord.WHERE);
        sql.append(SQLKeyWord.SPACE);
        sql.append(fieldColumn.get(idField.getName()));
        sql.append(SQLKeyWord.EQUAL);
        sql.append(SQLKeyWord.PLACEHOLDER);
        System.out.println(sql.toString());
        return sql.toString();
    }

    private String buildGetAllSql() {
        StringBuffer sql = new StringBuffer();
        sql = selectAllFrom(sql);
        sql.append(tableName);
        System.out.println(sql.toString());
        return sql.toString();
    }

    private String buildPKSql() throws JDBCException {
        StringBuffer sql = new StringBuffer();
        if (Integer.class.equals(idField.getType()) || int.class.equals(idField.getType())) {
            GeneratedValue generatedValue = idField.getAnnotation(GeneratedValue.class);
            if (generatedValue != null) {
                if (!GenerationType.IDENTITY.equals(generatedValue.strategy())) {
                    throw new JDBCException("GeneratedValue.strategy is not GenerationType.IDENTITY");
                }
            }
            sql.append(SQLKeyWord.SELECT);
            sql.append(SQLKeyWord.SPACE);
            sql.append(SQLKeyWord.MAX);
            sql.append(SQLKeyWord.L_PARENTHESE);
            sql.append(idField.getName());
            sql.append(SQLKeyWord.R_PARENTHESE);
            sql.append(SQLKeyWord.SPACE);
            sql.append(SQLKeyWord.FROM);
            sql.append(SQLKeyWord.SPACE);
            sql.append(tableName);
            System.out.println(sql.toString().toUpperCase());
        } else {
            throw new JDBCException("ID class fieldColumn.get(fieldName) is not int or Interger");
        }
        return sql.toString();
    }

    public Class getClazz() {
        Class clazz = null;
        Type type = getClass().getGenericSuperclass();
        if (type instanceof Class) {
            clazz = (Class<?>) getClass().getGenericSuperclass();
        } else if (type instanceof ParameterizedType) {
            ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
            Type[] types = genericSuperclass.getActualTypeArguments();
            if (types[0] instanceof ParameterizedType) {
                // If the class has parameterized types, it takes the raw fieldColumn.get(fieldName).
                ParameterizedType parameterizedType = (ParameterizedType) types[0];
                clazz = (Class<?>) parameterizedType.getRawType();
            } else {
                clazz = (Class<?>) types[0];
            }
        }
        return clazz;
    }

    public static void main(String[] args) {
        BaseDaoImpl<Test, Integer> dao = new BaseDaoImpl<Test, Integer>();
        System.out.println(dao.getClazz());
    }

    private StringBuffer selectAllFrom(StringBuffer sql) {
        sql.append(SQLKeyWord.SELECT);
        sql.append(SQLKeyWord.SPACE);
        sql.append(SQLKeyWord.STAR);
        sql.append(SQLKeyWord.SPACE);
        sql.append(SQLKeyWord.FROM);
        sql.append(SQLKeyWord.SPACE);
        return sql;
    }

    private StringBuffer replaceEndString(StringBuffer sql, String str) {
        return sql.replace(sql.length() - 1, sql.length(), str);
    }

    private String getMethodName(String fieldName) {
        return GET_METHOD_PREFIX + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1, fieldName.length());
    }

    private String setMethodName(String fieldName) {
        return SET_METHOD_PREFIX + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1, fieldName.length());
    }

    private int getStartRow(int page, int pageSize) {
        return (page - 1) * pageSize;
    }

    public Object handleFuzzyQueryCriteria(Object example) {
        List<Integer> checkList = new ArrayList<Integer>();
        return traverseCriteriaObject(example, checkList);
    }

    private Object traverseCriteriaObject(Object example, List<Integer> checkList) {
        if (example == null) {
            return null;
        }
        //to avoid duplicated object traveling.
        if (checkList.contains(example.hashCode())) {
            LOGGER.trace("Object  has been handled." + example);
            return example;
        } else {
            checkList.add(example.hashCode());
        }
        if (example instanceof Map) {
            LOGGER.trace("BeanUtils can not clone Map object.");
            return example;
        }
        if (example instanceof Collection) {
            LOGGER.trace("BeanUtils can not clone Collection object.");
            return example;
        }
        LOGGER.trace("start to handle fuzzy query field....");
        Assert.notNull(example, "Example object can not be empty.");
        Object cle = example;
        try {
            LOGGER.trace("Object before handling: {}" + BeanUtils.describe(example).toString());
            cle = BeanUtils.cloneBean(example);
            Field[] fields = cle.getClass().getDeclaredFields();
            Method[] methods = cle.getClass().getDeclaredMethods();
            for (Field field : fields) {
                boolean isFuzzyField = field.isAnnotationPresent(FuzzyQuery.class);
                if (isFuzzyField) {
                    if (field.getType().isAssignableFrom(String.class)) {
                        Object value = null;
                        field.setAccessible(true);
                        value = field.get(example);
                        if (StringUtils.isNotEmptyString((String) value)) {
                            Object newVal = "%" + StringUtils.trim((String) value) + "%";
                            field.set(cle, newVal);
                        }
                    }
                }
                //                if ((IEntity.class).isAssignableFrom(field.getType())) {
                //                    LOGGER.trace("Found PO attribute {}, start traversing PO field."+field.getName());
                //                    Object value = null;
                //                    field.setAccessible(true);
                //                    value = field.get(example);
                //                    if (value != null) {
                //                        Object newVal = traverseCriteriaObject(value, checkList);
                //                        field.set(cle, newVal);
                //                    }
                //                }
            }
            //handle annotation in getter methods...
            for (Method method : methods) {
                method.setAccessible(true);
                boolean isFuzzyField = method.isAnnotationPresent(FuzzyQuery.class);
                if (isFuzzyField) {
                    if (method.getReturnType().isAssignableFrom(String.class)) {
                        Object value = null;
                        method.setAccessible(true);
                        value = method.invoke(example);

                        String property = StringUtils.uncapitalize(method.getName().replaceFirst("get", StringUtils.EMPTY_STR));
                        if (StringUtils.isNotEmptyString((String) value)) {
                            Object newVal = "%" + StringUtils.trim((String) value) + "%";
                            BeanUtils.setProperty(cle, property, newVal);
                        }
                    }
                }

            }
            LOGGER.trace("Object after handling: " + BeanUtils.describe(cle).toString());
        } catch (Exception e) {
            LOGGER.error("Error handling fuzzy query.", e);
        }

        return cle;

    }
}
