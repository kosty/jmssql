package org.jmssql;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.jmssql.util.Files;

public class Config {

    private String dbaseDriverName = "net.sourceforge.jtds.jdbc.Driver";
    private String url;
    private String user;
    private String password;

    public static Config parse(File tildeExpand) {
        Config opts = new Config();
        Properties config = null;
        try {
            config = Files.loadProperties(tildeExpand);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        opts.url = config.getProperty("url");
        opts.user = config.getProperty("user");
        opts.password = config.getProperty("password");
        return opts;
    }

    public DataSource getDataSource() {
        BasicDataSource bds = new BasicDataSource();
        bds.setDriverClassName(dbaseDriverName);
        bds.setUrl(url);
        bds.setUsername(user);
        bds.setPassword(password);
        bds.setTestWhileIdle(true);
        bds.setMinEvictableIdleTimeMillis(1000);
        bds.setTimeBetweenEvictionRunsMillis(1000);
        bds.setNumTestsPerEvictionRun(1000);
        return bds;
    }

}
