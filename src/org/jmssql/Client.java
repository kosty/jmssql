package org.jmssql;

import static org.jmssql.util.Files.tildeExpand;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.jmssql.query.Execute;
import org.jmssql.tx.TXHelper;
import org.jmssql.tx.TXHelperImpl;

public class Client {

    static {
        setDefaultUncaughtExceptionHandler();
        initDebugLogger();
    }

    public static void setDefaultUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println("Uncaught exception in: " + t);
                System.out.println(e);
                e.printStackTrace();
                System.exit(123);
            }
        });
    }

    public static void initDebugLogger() {
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure();
        Enumeration appenders = Logger.getRootLogger().getAllAppenders();
        while (appenders.hasMoreElements()) {
            Appender a = (Appender) appenders.nextElement();
            a.setLayout(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss.SSS} %p %c{2} %m%n"));
        }

        Logger.getRootLogger().setLevel(Level.DEBUG);
    }

    public static File sqlFile;
    public static String confFile;

    public static void main(String[] args) {
        List<String> a = new ArrayList<String>(Arrays.asList(args));
        if (a.size() < 4) {
            usage();
        }

        while (!a.isEmpty()) {
            String key = a.remove(0);
            String val = a.remove(0);
            if ("--sqlfile".equals(key)) {
                sqlFile = tildeExpand(val);
                if (!sqlFile.exists()) {
                    System.err.println("File '" + sqlFile.getAbsolutePath() + "' does not exist");
                    System.exit(1);
                }
            } else if ("--config".equals(key)) {
                confFile = val;
            } else {
                usage();
            }
        }

        final Config opts = Config.parse(tildeExpand(confFile));
        final List<String> sql = new ArrayList<String>();
        try {
            sql.addAll(FileUtils.readLines(sqlFile));
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(1);
        }
        final DataSource dataSource = opts.getDataSource();

        try {
            TXHelperImpl txHelper = new TXHelperImpl(dataSource);
            txHelper.run(new TXHelper.R() {
                public void run() throws Exception {
                    for (String anSql : sql) {
                        Execute.update(dataSource, anSql);
                    }
                }
            });

        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(3);
        }
    }

    private static void usage() {
        System.err
                .println("usage: java -cp jmssql.jar org.jmssql.Client --sqlfile <filename.sql> --config <jmssql.config>");
        System.exit(1);
    }
}
