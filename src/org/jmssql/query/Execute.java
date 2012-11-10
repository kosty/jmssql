package org.jmssql.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.sql.DataSource;

public class Execute {

    public static Info update(DataSource ds, String sql) {
        Connection con = null;
        PreparedStatement stmt = null;
        try {

            System.out.println(sql);

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
        } catch (SQLException e) {
            throw new RuntimeException("Couldn't execute update: '" + sql + "'", e);
        } finally {
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
