package org.github.wks.jhql.query;

import java.util.Map;

import org.github.wks.jhql.query.annotation.Required;
import org.jaxen.JaxenException;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Node;

public class SingleQueryer implements Queryer 
{
    private DOMXPath fromExpr;
    private Queryer mapper;
    
    @Required
    public void setFrom(String fromExprStr) {
        try {
            this.fromExpr = new DOMXPath(fromExprStr);
        } catch (JaxenException e) {
            throw new IllegalArgumentException(
                    "Illegal xPath in 'from' clause: " + fromExpr, e);
        }       
    }
    
    @Required
    public void setSelect(Queryer mapper) {
        this.mapper = mapper;
    }
    
    public SingleQueryer() {
    }

    public SingleQueryer(String fromExpr, Queryer mapper) {
        this.setFrom(fromExpr);
        this.setSelect(mapper);
    }

    public Object query(Node node, Map<String, Object> context) {
        Node from;
        try {
            from = (Node) fromExpr.selectSingleNode(node);
        } catch (JaxenException e) {
            throw new ParsingException("Error applying the 'from' part "
                    + fromExpr + "to node " + node, e);
        }
        Object result = null;
        if (from != null) {
            result = mapper.query(from, context);
        }
        return result;
    }

}