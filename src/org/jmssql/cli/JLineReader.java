package org.jmssql.cli;

import java.io.IOException;

import jline.console.ConsoleReader;
import jline.console.completer.StringsCompleter;

public class JLineReader implements LineReader {

    ConsoleReader reader;
    private String[] mssqlKeywords = { "select", "update", "delete", "insert" };

    public JLineReader() throws IOException {
        reader = new ConsoleReader();
        reader.addCompleter(new StringsCompleter(mssqlKeywords));
        reader.setPrompt("jmssql> ");
        reader.setHistoryEnabled(true);
    }

    @Override
    public String nextLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * MS SQL reserved keywords
     * add external procedure all fetch public alter file raiserror and fillfactor read any for readtext as foreign reconfigure asc freetext references authorization freetexttable replication backup from restore begin full restrict between function return break goto revert browse grant revoke bulk group right by having rollback cascade holdlock rowcount case identity rowguidcol check
     * identity_insert rule checkpoint identitycol save close if schema clustered in securityaudit coalesce index select collate inner semantickeyphrasetable column insert semanticsimilaritydetailstable commit intersect semanticsimilaritytable compute into session_user constraint is set contains join setuser containstable key shutdown continue kill some convert left statistics create like
     * system_user cross lineno table current load tablesample current_date merge textsize current_time national then current_timestamp nocheck to current_user nonclustered top cursor not tran database null transaction dbcc nullif trigger deallocate of truncate declare off try_convert default offsets tsequal delete on union deny open unique desc opendatasource unpivot disk openquery update
     * distinct openrowset updatetext distributed openxml use double option user drop or values dump order varying else outer view end over waitfor errlvl percent when escape pivot where except plan while exec precision with execute primary within group exists print writetext exit proc
     */
}
