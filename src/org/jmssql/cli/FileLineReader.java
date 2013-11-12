package org.jmssql.cli;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.io.FileUtils;

public class FileLineReader implements LineReader {
    final List<String> sql = new ArrayList<String>();
    final Iterator<String> iter;

    public FileLineReader(File sqlFile) throws IOException {
        sql.addAll(FileUtils.readLines(sqlFile));
        iter = sql.iterator();
    }

    @Override
    public String nextLine() {
        String next = null;
        try {
            next = iter.next();
        } catch (NoSuchElementException nsee) {
            next = null;
        }
        return next;
    }

}
