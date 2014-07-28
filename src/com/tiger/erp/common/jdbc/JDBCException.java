package com.tiger.erp.common.jdbc;

public class JDBCException extends Exception {

    public JDBCException() {
    }

    public JDBCException(String message) {
        super(message);
    }

    public JDBCException(Throwable cause) {
        super(cause);
    }

    public JDBCException(String message, Throwable cause) {
        super(message, cause);
    }

}
