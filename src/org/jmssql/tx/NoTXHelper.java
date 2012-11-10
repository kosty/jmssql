package org.jmssql.tx;

import javax.sql.DataSource;

public class NoTXHelper implements TXHelper {
    private DataSource ds;

    public NoTXHelper(DataSource ds) {
        this.ds = ds;
    }

    public void run(R r) {

        try {
            r.run();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public DataSource dataSource() {
        return ds;
    }
}