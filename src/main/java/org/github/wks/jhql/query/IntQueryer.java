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
