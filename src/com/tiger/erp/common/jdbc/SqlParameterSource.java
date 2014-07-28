package com.tiger.erp.common.jdbc;


public interface SqlParameterSource {
    /**
     * Constant that indicates an unknown (or unspecified) SQL type.
     * To be returned from {@code getType} when no specific SQL type known.
     * @see #getSqlType
     * @see java.sql.Types
     */
    int TYPE_UNKNOWN =  Integer.MIN_VALUE;


    /**
     * Determine whether there is a value for the specified named parameter.
     * @param paramName the name of the parameter
     * @return whether there is a value defined
     */
    boolean hasValue(String paramName);

    /**
     * Return the parameter value for the requested named parameter.
     * @param paramName the name of the parameter
     * @return the value of the specified parameter
     * @throws IllegalArgumentException if there is no value for the requested parameter
     */
    Object getValue(String paramName) throws IllegalArgumentException;

    /**
     * Determine the SQL type for the specified named parameter.
     * @param paramName the name of the parameter
     * @return the SQL type of the specified parameter,
     * or {@code TYPE_UNKNOWN} if not known
     * @see #TYPE_UNKNOWN
     */
    int getSqlType(String paramName);

    /**
     * Determine the type name for the specified named parameter.
     * @param paramName the name of the parameter
     * @return the type name of the specified parameter,
     * or {@code null} if not known
     */
    String getTypeName(String paramName);

}

