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
	private boolean trim = true;

	public void setTrim(boolean trim) {
		this.trim = trim;
	}

	public TextQueryer() {
	}

	public TextQueryer(String xPathExpression) throws IllegalArgumentException {
		this.setValue(xPathExpression);
	}
	

	public String query(Node node, Map<String, Object> context) {
		String result = queryWithGrep(node);
		if(trim) {
			result = result.trim();
		}
		return result;
	}

}
