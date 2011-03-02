package org.github.wks.jhql.query;

import java.util.ArrayList;
import java.util.List;

import org.jaxen.JaxenException;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Node;

/**
 * A Queryer that returns a List.
 * <p>
 * 
 * It performs one XPath query on a DOM node and gets multiple nodes. Then it
 * applies another JHQL query on each of the resulting node.
 */
public class ListQueryer implements Queryer {
	private final DOMXPath fromExpr;
	private final Queryer mapper;

	public ListQueryer(String fromExpr, Queryer mapper) {
		try {
			this.fromExpr = new DOMXPath(fromExpr);
		} catch (JaxenException e) {
			throw new IllegalArgumentException(
					"Illegal xPath in 'from' clause: " + fromExpr, e);
		}
		this.mapper = mapper;

	}

	@SuppressWarnings("unchecked")
	public List<Object> query(Node node) {
		List<Node> froms;
		try {
			froms = fromExpr.selectNodes(node);
		} catch (JaxenException e) {
			throw new ParsingException("Error applying the 'from' part "
					+ fromExpr + "to node " + node, e);
		}
		System.err.println("Querying from " + froms);
		List<Object> results = new ArrayList<Object>();
		for (Node n : froms) {
			Object r = mapper.query(n);
			results.add(r);
		}
		return results;
	}

}
