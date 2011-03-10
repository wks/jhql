package org.github.wks.jhql.query;

import java.util.Map;

/**
 * A Queryer that returns an integer.
 * <p>
 * It works like the TestQueryer, but converts the result into an integer. If
 * the result of text querying is not an integer, it returns null.
 */
public class IntQueryer extends XPathQueryer<Integer> {

	public IntQueryer() {
	}

	public IntQueryer(String xPathExpression) throws IllegalArgumentException {
		this.setValue(xPathExpression);
	}

	public Integer convert(Object object, Map<String, Object> context) {
		String result = ((String) object).trim();
		try {
			return new Integer(result);
		} catch (NumberFormatException e) {
			return null;
		}
	}

}
