package org.jmssql.tx;

import java.sql.SQLException;
import java.sql.Savepoint;

import javax.sql.DataSource;

import org.jmssql.tx.TXHelper.R;

public class TXHelperImpl {
    private final DataSource ds;
    private final TXDataSource txDs;
    private static ThreadLocal<Transaction> tl = new ThreadLocal<Transaction>();
    private final static boolean debug = true;

    public TXHelperImpl(DataSource ds) {
        this.ds = ds;
        this.txDs = new TXDataSource(ds);
    }

    static Transaction getCurrentTransaction() {
        return tl.get();
    }

    static void setCurrentTransaction(Transaction tx) {
        tl.set(tx);
    }

    public void run(R r) {
        try {
            Transaction tx = startOrJoin();

            execute(r, tx);

            commitOrHandOver(tx);
        } catch (Throwable t) {
            throw new RuntimeException("Could not execute transaction", t);
        }
    }

    private Transaction startOrJoin() throws SQLException {
        Transaction tx = getCurrentTransaction();

        if (tx == null) {
            debug("Starting transaction: " + Thread.currentThread().getId());
            tx = new Transaction(ds.getConnection());
            tx.getDelegate().setAutoCommit(false);
            setCurrentTransaction(tx);
        } else
            debug("Joining transaction: " + Thread.currentThread().getId());
        return tx;
    }

    private void commitOrHandOver(Transaction tx) throws Throwable {
        if (tx.atTheTop()) {
            debug("Finalizing transaction: " + Thread.currentThread().getId());
            tx.getDelegate().commit();
            cleanup(tx);
        } else
            debug("Avoiding finalize: " + Thread.currentThread().getId());
    }

    private void execute(R r, Transaction tx) throws Throwable {
        tx.goDown();

        Savepoint sp = null;
        try {
            sp = tx.getDelegate().setSavepoint();
            r.run();
        } catch (Throwable t) {
            rollback(tx, sp);
            throw t;
        } finally {
            tx.goUp();
        }
    }

    private void rollback(Transaction tx, Savepoint sp) throws SQLException, Throwable {
        debug("Rolling back transaction: " + Thread.currentThread().getId());
        if (tx.atTheTop()) {
            tx.getDelegate().rollback();
            cleanup(tx);
        } else {
            if (sp != null)
                tx.getDelegate().rollback(sp);
        }
    }

    private void cleanup(Transaction tx) throws Throwable {
        setCurrentTransaction(null);
        tx.getDelegate().close();
    }

    private static void debug(String str) {
        if (!debug)
            return;
        System.out.println(str);
    }

    public DataSource dataSource() {
        return txDs;
    }
}