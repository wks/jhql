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
 * A Queryer for String results.
 * <p>
 * It makes an XPath query on a DOM node and returns the concatenated text
 * contents in all matching nodes.
 */
public class TextQueryer extends XPathQueryer<String> {
	private boolean trim = true;

	public void setTrim(boolean trim) {
		this.trim = trim;
	}

	public TextQueryer() {
	}

	public TextQueryer(String xPathExpression) throws IllegalArgumentException {
		this.setValue(xPathExpression);
	}
	

	public String convert(Object obj, Map<String, Object> context) {
		String result = (String)obj;
		if(trim) {
			result = result.trim();
		}
		return result;
	}

}
