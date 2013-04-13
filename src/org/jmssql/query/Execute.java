package org.jmssql.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

public class Execute {
    
    private final static Logger log = Logger.getLogger(Execute.class);

    public static Info update(DataSource ds, String sql) throws SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        try {

            log.info(sql);

            con = ds.getConnection();
            stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            int updatedRows = stmt.executeUpdate();
            ResultSet keysRs = stmt.getGeneratedKeys();
            ArrayList<Object> keys = new ArrayList<Object>();

            while (keysRs.next()) {
                Object object = keysRs.getObject(1);
                keys.add(object);
            }

            return new Info(updatedRows, keys.toArray());
        } finally {
            closeQuiet(stmt);
            closeQuiet(con);
        }
    }
    
    public static <ResultType> ResultType query(DataSource ds, H<ResultType> handler, String sql) throws SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            log.info(sql);
            
            con = ds.getConnection();
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            return handler.handleResult(rs);
        } finally {
            closeQuiet(rs);
            closeQuiet(stmt);
            closeQuiet(con);
        }
    }

    public static void closeQuiet(Statement stmt) {
        if (stmt == null)
            return;
        try {
            stmt.close();
        } catch (Exception e) {
        }
    }

    public static void closeQuiet(Connection con) {
        if (con == null)
            return;
        try {
            con.close();
        } catch (Exception e) {
        }
    }
    
    public static void closeQuiet(ResultSet rs) {
        if (rs == null)
            return;
        try {
            rs.close();
        } catch (Exception e) {
        }
    }
    
    static interface H<ResultType> {
        ResultType handleResult(ResultSet rs) throws SQLException;
    }
    
    public abstract static class ListOf<ResultType> implements H<List<ResultType>> {
        public List<ResultType> handleResult(ResultSet rs) throws SQLException {
            ArrayList<ResultType> result = new ArrayList<ResultType>();
            while (rs.next())
                result.add(extract(rs));
            return result;
        }

        protected abstract ResultType extract(ResultSet rs) throws SQLException;
    }
    
    public abstract static class Single<ResultType> implements H<ResultType> {
        public ResultType handleResult(ResultSet rs) throws SQLException {
            if (rs.next())
                return extract(rs);
            return null;
        }

        protected abstract ResultType extract(ResultSet rs) throws SQLException;
    }

    public static class Info {

        public final int rowsModified;

        public final Object[] newIds;

        private Info(int count, Object[] ids) {
            this.rowsModified = count;
            this.newIds = ids;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("records affected: ");
            sb.append(rowsModified).append(" ids [");

            for (Object id : newIds)
                sb.append(" ").append(id);
            sb.append("]");
            return sb.toString();
        }

    }

}
