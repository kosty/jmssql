package org.jmssql.tx;

import java.sql.Connection;
import java.sql.SQLException;

class Transaction extends NoTXConnection {
    private int level;

    public Transaction(Connection c) {
        super(c);
    }

    public void goDown() {
        level++;
    }

    public void goUp() {
        level--;
    }

    public boolean atTheTop() {
        return level == 0;
    }

    public void close() throws SQLException {
        // DO nothing
        // TX helper will close the connection on TX finish
    }
}