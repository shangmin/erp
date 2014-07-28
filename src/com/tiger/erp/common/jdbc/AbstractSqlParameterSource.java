package com.tiger.erp.common.jdbc;

import java.util.HashMap;
import java.util.Map;

import com.tiger.erp.util.Assert;

public abstract class AbstractSqlParameterSource implements SqlParameterSource {

    private final Map<String, Integer> sqlTypes = new HashMap<String, Integer>();

    private final Map<String, String> typeNames = new HashMap<String, String>();


    /**
     * Register a SQL type for the given parameter.
     * @param paramName the name of the parameter
     * @param sqlType the SQL type of the parameter
     */
    public void registerSqlType(String paramName, int sqlType) {
        Assert.notNull(paramName, "Parameter name must not be null");
        this.sqlTypes.put(paramName, sqlType);
    }

    /**
     * Register a SQL type for the given parameter.
     * @param paramName the name of the parameter
     * @param typeName the type name of the parameter
     */
    public void registerTypeName(String paramName, String typeName) {
        Assert.notNull(paramName, "Parameter name must not be null");
        this.typeNames.put(paramName, typeName);
    }

    /**
     * Return the SQL type for the given parameter, if registered.
     * @param paramName the name of the parameter
     * @return the SQL type of the parameter,
     * or {@code TYPE_UNKNOWN} if not registered
     */
    public int getSqlType(String paramName) {
        Assert.notNull(paramName, "Parameter name must not be null");
        Integer sqlType = this.sqlTypes.get(paramName);
        if (sqlType != null) {
            return sqlType;
        }
        return TYPE_UNKNOWN;
    }

    /**
     * Return the type name for the given parameter, if registered.
     * @param paramName the name of the parameter
     * @return the type name of the parameter,
     * or {@code null} if not registered
     */
    public String getTypeName(String paramName) {
        Assert.notNull(paramName, "Parameter name must not be null");
        return this.typeNames.get(paramName);
    }

}

