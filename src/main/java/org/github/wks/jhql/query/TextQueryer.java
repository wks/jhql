package org.github.wks.jhql.query;

import java.util.Map;

import org.w3c.dom.Node;

/**
 * A Queryer for String results.
 * <p>
 * It makes an XPath query on a DOM node and returns the concatenated text
 * contents in all matching nodes.
 */
public class TextQueryer extends XPathQueryer {
	public TextQueryer() {
	}

	public TextQueryer(String xPathExpression) throws IllegalArgumentException {
		this.setValue(xPathExpression);
	}

	public String query(Node node, Map<String, Object> context) {
		return queryWithGrep(node);
	}

}
