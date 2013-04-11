package org.jmssql.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Repl implements LineReader {
    
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    
    @Override
    public String nextLine() {
        try {
            return in.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
