package org.jmssql.tx;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * Take whatever is in threadlocal
 * 
 * otherwise return wrapped connection from the underlying datasource ( with
 * disabled transactions )
 * 
 * @author Stanislav Vitvitskiy
 * 
 */
class TXDataSource implements DataSource {
    private final DataSource wrapped;

    public TXDataSource(DataSource wrapped) {
        this.wrapped = wrapped;
    }

    public Connection getConnection() throws SQLException {
        Connection con = TXHelperImpl.getCurrentTransaction();
        if (con != null)
            return con;
        return new NoTXConnection(wrapped.getConnection());
    }

    public Connection getConnection(String arg0, String arg1) throws SQLException {
        throw new UnsupportedOperationException("Stan");
    }

    public PrintWriter getLogWriter() throws SQLException {
        throw new UnsupportedOperationException("Stan");
    }

    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException("Stan");
    }

    public void setLogWriter(PrintWriter arg0) throws SQLException {
        throw new UnsupportedOperationException("Stan");
    }

    public void setLoginTimeout(int arg0) throws SQLException {
        throw new UnsupportedOperationException("Stan");
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException("Stan");
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("Stan");
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException("Stan");
    }
}
