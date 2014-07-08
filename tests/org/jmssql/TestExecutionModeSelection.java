package org.jmssql;

import static org.jmssql.Client.RunSqlLine.producesOutput;
import junit.framework.Assert;

import org.junit.Test;

public class TestExecutionModeSelection {

    @Test
    public void test() {
        Assert.assertTrue(producesOutput("select count(t.TitleID) from [172.31.214.231].[SonyDBB_CH2].dbo.Title t where t.updateddate > '2013-11-03'"));
        Assert.assertTrue(producesOutput("select count(t.TitleID) from [172.31.214.231].[SonyDBB_CH2].dbo.Title t where t.update > '2013-11-03'"));
        Assert.assertTrue(producesOutput("select count(t.TitleID) from [172.31.214.231].[SonyDBB_CH2].dbo.Title t where t.into > '2013-11-03'"));
        Assert.assertFalse(producesOutput("update [172.31.214.231].[SonyDBB_CH2].dbo.Title t set t.TitleName = 'a' where t.update > '2013-11-03'"));
        Assert.assertTrue(producesOutput("exec [172.31.214.20].[SonyDBB_Prod].[VAL].[GetTitleVideoRatingByTitleID] 'DA0D2FB4-901E-48F9-BE59-EC545DE965B8';"));
    }

}
