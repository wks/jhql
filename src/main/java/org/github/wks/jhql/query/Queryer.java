package org.github.wks.jhql.query;

import org.w3c.dom.*;

/**
 * A Queryer that makes JHQL querys on a DOM node.
 */
public interface Queryer 
{
	/**
	 * Query on a DOM node.
	 * @param node The DOM node to query.
	 * @return The returning object of the query.
	 */
    Object query(Node node);
}
