package org.github.wks.jhql;

import junit.framework.Assert;

import org.github.wks.jhql.query.Queryer;
import org.junit.Test;

public class AttrQueryTest
{

    @Test
    public void testQueryAttributeValue()
    {
        Jhql jhql = new Jhql();
        
        Queryer queryer = jhql.makeQueryer(
                "{ \"_type\": \"text\", \"value\": \"//a\", \"attr\": \"href\" }");
        
        Object result = jhql.queryHtml(queryer, "<a href='http://anjlab.com'>AnjLab</a>");

        Assert.assertEquals("http://anjlab.com", result);
    }
    

    @Test
    public void testQueryAttributeValueWithGrep()
    {
        Jhql jhql = new Jhql();
        
        Queryer queryer = jhql.makeQueryer(
                "{ \"_type\": \"text\", \"value\": \"//a\", \"attr\": \"href\", \"grep\": \"http://(.*).com\" }");
        
        Object result = jhql.queryHtml(queryer, "<a href='http://anjlab.com'>AnjLab</a>");

        Assert.assertEquals("anjlab", result);
    }

}
