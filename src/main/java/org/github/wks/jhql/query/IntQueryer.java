package org.github.wks.jhql.query;

import java.util.Map;

import org.w3c.dom.Node;

/**
 * A Queryer that returns an integer.
 * <p>
 * It works like the TestQueryer, but converts the result into an integer. If
 * the result of text querying is not an integer, it returns null.
 */
public class IntQueryer extends XPathQueryer {
	
	public IntQueryer() {
	}

	public IntQueryer(String xPathExpression) throws IllegalArgumentException {
		this.setValue(xPathExpression);
	}

	public Integer query(Node node, Map<String, Object> context) {
		String result = this.queryWithGrep(node).trim();
		try {
			return new Integer(result);
		} catch (NumberFormatException e) {
			return null;
		}
	}

}
