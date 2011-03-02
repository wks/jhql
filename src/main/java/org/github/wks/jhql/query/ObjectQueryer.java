package org.github.wks.jhql.query;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Node;

/**
 * A Queryer that does many different JHQL querys on the same DOM node. It
 * returns a map with each key mapping to its corresponding result.
 */
public class ObjectQueryer implements Queryer {

	private final Map<String, Queryer> fieldRules;

	public ObjectQueryer(Map<String, Queryer> fieldRules) {
		this.fieldRules = fieldRules;
	}

	public Map<String, Object> query(Node node) {
		Map<String, Object> result = new HashMap<String, Object>();
		for (Map.Entry<String, Queryer> rulePair : fieldRules.entrySet()) {
			Object fieldResult = rulePair.getValue().query(node);
			result.put(rulePair.getKey(), fieldResult);
		}
		return result;
	}

}
