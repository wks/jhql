package org.github.wks.jhql.query;

import java.util.List;
import org.jaxen.JaxenException;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Node;

public class TextQueryer implements Queryer {
	private DOMXPath xPath;

	public TextQueryer(String xPathExpression) throws IllegalArgumentException {
		try {
			xPath = new DOMXPath(xPathExpression);
		} catch (JaxenException e) {
			throw new IllegalArgumentException("Illegal xPath: "
					+ xPathExpression, e);
		}
	}

	public String query(Node node) {
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
