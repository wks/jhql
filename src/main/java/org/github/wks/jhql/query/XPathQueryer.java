/*
 *   Copyright 2011,2012 Kunshan Wang
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
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
