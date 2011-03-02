package org.github.wks.jhql.query;

import org.w3c.dom.Node;

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
