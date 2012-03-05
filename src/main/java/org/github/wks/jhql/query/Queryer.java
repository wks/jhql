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

import org.w3c.dom.*;

/**
 * A Queryer that makes JHQL querys on a DOM node.
 */
public interface Queryer 
{
	/**
	 * Query on a DOM node.
	 * @param node The DOM node to query.
	 * @param context The context of the query.
	 * @return The returning object of the query.
	 */
    Object query(Node node, Map<String, Object> context);
}
