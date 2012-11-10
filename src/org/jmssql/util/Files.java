package org.jmssql.util;

import static org.apache.commons.lang.SystemUtils.getUserHome;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

public class Files {

    public static File tildeExpand(String path) {
        if (path.startsWith("~")) {
            path = path.replaceFirst("~", getUserHome().getAbsolutePath());
        }
        return new File(path);
    }

    public static Properties loadProperties(File file) throws IOException {
        Reader reader = null;
        try {
            Properties props = new Properties();
            if (file.exists()) {
                reader = new BufferedReader(new FileReader(file));
                props.load(reader);
            }
            return props;
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

}
