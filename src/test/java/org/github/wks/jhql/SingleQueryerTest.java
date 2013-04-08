package org.github.wks.jhql;

import java.util.Map;

import junit.framework.Assert;

import org.github.wks.jhql.query.Queryer;
import org.junit.Test;

public class SingleQueryerTest
{

    @Test
    public void testQuery()
    {
        Jhql jhql = new Jhql();
        
        Queryer queryer = jhql.makeQueryer(
                "{" +
                    "\"key1\": { \"_type\": \"single\", \"from\": \"//td[text() = 'key1']\", \"select\": \"text:../td[2]\" }, " +
                    "\"key2\": { \"_type\": \"single\", \"from\": \"//td[text() = 'key2']\", \"select\": { \"value\": \"text:../td[2]\" } }" +
                "}");
        
        Map<?, ?> result = (Map<?, ?>) jhql.queryHtml(queryer,
                "<table>" +
                    "<tr>" +
                        "<td>key1</td>" +
                        "<td>value1</td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td>key2</td>" +
                        "<td>value2</td>" +
                    "</tr>" +
                "</table>");

        Assert.assertEquals(2, result.size());
        Assert.assertEquals("value1", result.get("key1"));
        Assert.assertNotNull(result.get("key2"));
        Assert.assertEquals("value2", ((Map<?,?>) result.get("key2")).get("value"));
    }

}