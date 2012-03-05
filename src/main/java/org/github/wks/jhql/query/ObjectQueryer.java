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

import java.util.LinkedHashMap;
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

	public Map<String, Object> query(Node node, Map<String, Object> context) {
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		for (Map.Entry<String, Queryer> rulePair : fieldRules.entrySet()) {
			Object fieldResult = rulePair.getValue().query(node, context);
			result.put(rulePair.getKey(), fieldResult);
		}
		return result;
	}

}
