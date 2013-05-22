package org.jmssql;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.jmssql.util.Files.tildeExpand;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.jmssql.cli.FileLineReader;
import org.jmssql.cli.JLineReader;
import org.jmssql.cli.LineReader;
import org.jmssql.cli.Repl;
import org.jmssql.query.Execute;
import org.jmssql.query.Execute.ListOf;
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
        try {
            BasicConfigurator.configure(new FileAppender(null, File.createTempFile("jmssql-", "log").getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
            BasicConfigurator.configure();
        }
        Enumeration appenders = Logger.getRootLogger().getAllAppenders();
        while (appenders.hasMoreElements()) {
            Appender a = (Appender) appenders.nextElement();
            a.setLayout(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss.SSS} %p %c{2} %m%n"));
        }

        Logger.getRootLogger().setLevel(Level.DEBUG);
    }

    public static File sqlFile;
    public static String confFile;

    public static void main(String[] args) throws IOException {
        List<String> a = new ArrayList<String>(Arrays.asList(args));

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
        
        if (isBlank(confFile))
            usage();
            

        final Config opts = Config.parse(tildeExpand(confFile));

        final LineReader reader = pickReader();

        final DataSource dataSource = opts.getDataSource();

        try {
            TXHelperImpl txHelper = new TXHelperImpl(dataSource);
            txHelper.run(new TXHelper.R() {
                public void run() throws Exception {
                    String anSql = null;
                    while ((anSql = reader.nextLine()) != null) {
                        runSql(dataSource, anSql);
                    }
                }

            });

        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(3);
        }
    }

    private static void runSql(final DataSource dataSource, String anSql) {
        String canonicSql = anSql.trim().toLowerCase();
        try {
            if (canonicSql.startsWith("select") && !canonicSql.contains("into"))

                Execute.query(dataSource, new ListOf<List<String>>() {
                    @Override
                    protected List<String> extract(ResultSet rs) throws SQLException {
                        List<String> obj = new ArrayList<String>();
                        int columnCount = rs.getMetaData().getColumnCount();
                        for (int i = 1; i <= columnCount; i++) {
                            String val = rs.getString(i);
                            System.out.print(val + "\t");
                            obj.add(val);
                        }
                        System.out.println();
                        return obj;
                    }
                }, anSql);
            else
                Execute.update(dataSource, anSql);
        } catch (SQLException sqle){
            System.out.println(collectExceptionMessages(sqle)+" while running sql: "+anSql);
            
        } catch (RuntimeException re) {
            if (!isCousedBySQLException(re))
                throw re;

            System.out.println(collectExceptionMessages(re)+" while running sql: "+anSql);
        }
    }

    private static String collectExceptionMessages(Throwable re) {
        StringBuilder sb = new StringBuilder();
        while(re != null){
            sb.append(re.getMessage()).append("\n");
            re = re.getCause();
        }
        return sb.toString();
    }

    private static boolean isCousedBySQLException(Throwable re) {
        if (re instanceof SQLException)
            return true;
        
        if (re.getCause() == null)
            return false;
        
        return isCousedBySQLException(re.getCause());
    }
    
    

    private static LineReader pickReader() throws IOException {
        if (sqlFile != null) 
            return new FileLineReader(sqlFile);
        
        if (System.in.available() > 0)
            return new Repl();

        return new JLineReader();
    }

    private static void usage() {
        System.err.println("usage: java -cp jmssql.jar org.jmssql.Client [--sqlfile <filename.sql>] --config <jmssql.config>");
        System.exit(1);
    }
}
