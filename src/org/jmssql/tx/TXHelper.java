package org.jmssql.tx;

import javax.sql.DataSource;

public interface TXHelper {
    void run(R r);

    DataSource dataSource();

    public static interface R {
        void run() throws Exception;
    }
}