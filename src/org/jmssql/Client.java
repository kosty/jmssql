package org.jmssql;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.jmssql.util.Files.tildeExpand;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
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
import org.jmssql.query.Execute.TableLikeList;
import org.jmssql.tx.TXHelper;
import org.jmssql.tx.TXHelperImpl;

import au.com.bytecode.opencsv.CSVWriter;

import com.google.gson.GsonBuilder;

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
    private static String output;

    public static void main(String[] args) throws IOException {
        List<String> a = new ArrayList<String>(Arrays.asList(args));

        while (!a.isEmpty()) {
            String key = a.remove(0);
            if (a.isEmpty())
                usage();
            String val = a.remove(0);
            if ("--sqlfile".equals(key)) {
                sqlFile = tildeExpand(val);
                if (!sqlFile.exists()) {
                    System.err.println("File '" + sqlFile.getAbsolutePath() + "' does not exist");
                    System.exit(1);
                }
            } else if ("--config".equals(key)) {
                confFile = val;
            } else if ("--output".equals(key)) {

                output = val;
            } else {
                usage();
            }
        }

        if (isBlank(confFile))
            usage();

        final Config opts = Config.parse(tildeExpand(confFile));

        final LineReader reader = pickReader();

        final DataSource dataSource = opts.getDataSource();

        String anSql = null;
        TXHelperImpl txHelper = new TXHelperImpl(dataSource);
        while ((anSql = reader.nextLine()) != null) {
            try {
                txHelper.run(new RunSqlLine(dataSource, anSql));
            } catch (RuntimeException re) {
                if (!isCousedBySQLException(re))
                    throw re;

                if (opts.isDebugEnabled())
                    re.printStackTrace(System.err);

                if (!(reader instanceof JLineReader))
                    System.exit(-2);

                System.out.println(collectExceptionMessages(re) + " while running sql: " + anSql);

            } catch (Throwable e) {
                e.printStackTrace();
                System.exit(3);
            }
        }
    }

    private static void writeTabulated(List<String[]> results) {
        for (String[] entry : results) {
            for (int i = 0; i < entry.length; i++) {
                System.out.print(entry[i]);
                if (i < entry.length - 1)
                    System.out.print("\t");
            }
            System.out.println();
        }
    }

    private static void writeCSV(List<String[]> results) throws IOException {
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(System.out), ',', '\'', '\'');
        try {
            writer.writeAll(results);
            writer.flush();
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    private static String collectExceptionMessages(Throwable re) {
        StringBuilder sb = new StringBuilder();
        while (re != null) {
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
        System.err.println("usage: java -cp jmssql.jar org.jmssql.Client --config <jmssql.config> [--sqlfile <filename.sql>] [--output json|csv|tab|prettyjson|headedcsv|headedtab]");
        System.exit(1);
    }

    public static class RunSqlLine implements TXHelper.R {

        private String anSql;
        private DataSource dataSource;

        public RunSqlLine(final DataSource dataSource, String anSql) {
            this.dataSource = dataSource;
            this.anSql = anSql;

        }

        public void run() throws Exception {
            runSql(dataSource, anSql);
        }

        private void runSql(final DataSource dataSource, String anSql) throws SQLException, IOException {
            String canonicSql = anSql.trim().toLowerCase();

            if (producesOutput(canonicSql)) {

                if ("json".equals(output) || "prettyjson".equals(output)) {
                    List<Map<String, String>> result = Execute.query(dataSource, new Execute.MapingLikeList(), anSql);
                    GsonBuilder gb = new GsonBuilder();
                    gb.disableHtmlEscaping();
                    gb.serializeNulls();

                    if ("prettyjson".equals(output))
                        gb.setPrettyPrinting();
                    System.out.println(gb.create().toJson(result));

                } else {
                    List<String[]> results = Execute.query(dataSource, new TableLikeList("headedcsv".equals(output) || "headedtab".equals(output)), anSql);
                    if ("csv".equals(output) || "headedcsv".equals(output)) {
                        writeCSV(results);
                    } else {
                        writeTabulated(results);
                    }
                }
            } else
                Execute.update(dataSource, anSql);
        }
        
        public static final Pattern hasSelect = Pattern.compile(".*[\\s]*select[\\s].*");
        public static final Pattern hasInto = Pattern.compile(".*[\\s]into[\\s].*");
        public static final Pattern hasUpdate = Pattern.compile(".*[\\s]update[\\s].*");
        
        public static boolean producesOutput(String sql){
            boolean s = hasSelect.matcher(sql).matches(); 
            boolean i = hasInto.matcher(sql).matches();
            boolean u = hasUpdate.matcher(sql).matches();
            return s && !(i || u);
        }
    }

}
