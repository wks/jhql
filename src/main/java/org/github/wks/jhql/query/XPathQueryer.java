package org.github.wks.jhql.query;

import java.util.List;
import java.util.Map;

import org.github.wks.jhql.query.annotation.Required;
import org.jaxen.JaxenException;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Node;

public abstract class XPathQueryer<T> extends ScalarQueryer<T> {
	private DOMXPath xPath;

	@Required
	public void setValue(String xPathExpression)
			throws IllegalArgumentException {
		try {
			xPath = new DOMXPath(xPathExpression);
		} catch (JaxenException e) {
			throw new IllegalArgumentException("Illegal xPath: "
					+ xPathExpression, e);
		}
	}

	@Override
	protected String generate(Node node, Map<String,Object> context) {
		try {
			@SuppressWarnings("unchecked")
			List<Node> results = xPath.selectNodes(node);
			StringBuilder sb = new StringBuilder();
			for (Node n : results) {
				sb.append(n.getTextContent());
			}

			return sb.toString();
		} catch (JaxenException e) {
			throw new ParsingException("Error selecting " + xPath + " on "
					+ node, e);
		}
	}

}
