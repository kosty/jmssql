package org.jmssql.tx;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

class NoTXConnection implements java.sql.Connection {
    private Connection delegate;

    public NoTXConnection(Connection c) {
        this.delegate = c;
    }

    public Connection getDelegate() {
        return delegate;
    }

    public void commit() throws SQLException {
        throw new RuntimeException("Transactions are not allowed outside of TXHelper");
    }

    public void rollback() throws SQLException {
        throw new RuntimeException("Transactions are not allowed outside of TXHelper");
    }

    public void rollback(Savepoint savepoint) throws SQLException {
        throw new RuntimeException("Transactions are not allowed outside of TXHelper");
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        throw new RuntimeException("Transactions are not allowed outside of TXHelper");
    }

    public Savepoint setSavepoint() throws SQLException {
        throw new RuntimeException("Transactions are not allowed outside of TXHelper");
    }

    public Savepoint setSavepoint(String name) throws SQLException {
        throw new RuntimeException("Transactions are not allowed outside of TXHelper");
    }

    public void setTransactionIsolation(int level) throws SQLException {
        throw new RuntimeException("Transactions are not allowed outside of TXHelper");
    }

    public void clearWarnings() throws SQLException {
        delegate.clearWarnings();
    }

    public void close() throws SQLException {
        delegate.close();
    }

    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return delegate.createArrayOf(typeName, elements);
    }

    public Blob createBlob() throws SQLException {
        return delegate.createBlob();
    }

    public Clob createClob() throws SQLException {
        return delegate.createClob();
    }

    public NClob createNClob() throws SQLException {
        return delegate.createNClob();
    }

    public SQLXML createSQLXML() throws SQLException {
        return delegate.createSQLXML();
    }

    public Statement createStatement() throws SQLException {
        return delegate.createStatement();
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return delegate.createStatement(resultSetType, resultSetConcurrency);
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        return delegate.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return delegate.createStruct(typeName, attributes);
    }

    public boolean getAutoCommit() throws SQLException {
        return delegate.getAutoCommit();
    }

    public String getCatalog() throws SQLException {
        return delegate.getCatalog();
    }

    public Properties getClientInfo() throws SQLException {
        return delegate.getClientInfo();
    }

    public String getClientInfo(String name) throws SQLException {
        return delegate.getClientInfo(name);
    }

    public int getHoldability() throws SQLException {
        return delegate.getHoldability();
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        return delegate.getMetaData();
    }

    public int getTransactionIsolation() throws SQLException {
        return delegate.getTransactionIsolation();
    }

    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return delegate.getTypeMap();
    }

    public SQLWarning getWarnings() throws SQLException {
        return delegate.getWarnings();
    }

    public boolean isClosed() throws SQLException {
        return delegate.isClosed();
    }

    public boolean isReadOnly() throws SQLException {
        return delegate.isReadOnly();
    }

    public boolean isValid(int timeout) throws SQLException {
        return delegate.isValid(timeout);
    }

    public String nativeSQL(String sql) throws SQLException {
        return delegate.nativeSQL(sql);
    }

    public CallableStatement prepareCall(String sql) throws SQLException {
        return delegate.prepareCall(sql);
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return delegate.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetConcurrency);
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
            int resultSetHoldability) throws SQLException {
        return delegate.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return delegate.prepareStatement(sql);
    }

    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return delegate.prepareStatement(sql, autoGeneratedKeys);
    }

    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return delegate.prepareStatement(sql, columnIndexes);
    }

    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return delegate.prepareStatement(sql, columnNames);
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
            throws SQLException {
        return delegate.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
            int resultSetHoldability) throws SQLException {
        return delegate.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        delegate.releaseSavepoint(savepoint);
    }

    public void setCatalog(String catalog) throws SQLException {
        delegate.setCatalog(catalog);
    }

    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        delegate.setClientInfo(properties);
    }

    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        delegate.setClientInfo(name, value);
    }

    public void setHoldability(int holdability) throws SQLException {
        delegate.setHoldability(holdability);
    }

    public void setReadOnly(boolean readOnly) throws SQLException {
        delegate.setReadOnly(readOnly);
    }

    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        delegate.setTypeMap(map);
    }

    public boolean isWrapperFor(Class<?> arg0) throws SQLException {
        return delegate.isWrapperFor(arg0);
    }

    public <T> T unwrap(Class<T> arg0) throws SQLException {
        return delegate.unwrap(arg0);
    }

    public int getNetworkTimeout() throws SQLException {
        return 0;//delegate.getNetworkTimeout();
    }

    public void setSchema(String schema) throws SQLException {
        //delegate.setSchema(schema);
    }

    public String getSchema() throws SQLException {
        return null;//delegate.getSchema();
    }

    public void abort(Executor executor) throws SQLException {
//        delegate.abort(executor);

    }

    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
//        delegate.setNetworkTimeout(executor, milliseconds);
    }

}
