package org.github.wks.jhql.query;

import org.w3c.dom.Node;

/**
 * A Queryer that returns an integer.
 * <p>
 * It works like the TestQueryer, but converts the result into an integer. If
 * the result of text querying is not an integer, it returns null.
 */
public class IntQueryer implements Queryer {
	private TextQueryer textQueryer;

	public IntQueryer(String xPathExpression) throws IllegalArgumentException {
		textQueryer = new TextQueryer(xPathExpression);
	}

	public Integer query(Node node) {
		String result = textQueryer.query(node).trim();
		try {
			return new Integer(result);
		} catch (NumberFormatException e) {
			return null;
		}
	}

}
