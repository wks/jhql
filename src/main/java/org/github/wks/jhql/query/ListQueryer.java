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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.github.wks.jhql.query.annotation.Required;
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
	private DOMXPath fromExpr;
	private Queryer mapper;
	
	@Required
	public void setFrom(String fromExprStr) {
		try {
			this.fromExpr = new DOMXPath(fromExprStr);
		} catch (JaxenException e) {
			throw new IllegalArgumentException(
					"Illegal xPath in 'from' clause: " + fromExpr, e);
		}		
	}
	
	@Required
	public void setSelect(Queryer mapper) {
		this.mapper = mapper;
	}
	
	public ListQueryer() {
	}

	public ListQueryer(String fromExpr, Queryer mapper) {
		this.setFrom(fromExpr);
		this.setSelect(mapper);
	}

	@SuppressWarnings("unchecked")
	public List<Object> query(Node node, Map<String, Object> context) {
		List<Node> froms;
		try {
			froms = fromExpr.selectNodes(node);
		} catch (JaxenException e) {
			throw new ParsingException("Error applying the 'from' part "
					+ fromExpr + "to node " + node, e);
		}
		List<Object> results = new ArrayList<Object>();
		for (Node n : froms) {
			Object r = mapper.query(n, context);
			results.add(r);
		}
		return results;
	}

}
