package org.github.wks.jhql.query;

import java.util.Map;

import org.github.wks.jhql.query.annotation.Required;
import org.w3c.dom.Node;

public class ContextQueryer extends GrepableQueryer {
	private String value;

	@Required
	public String getValue() {
		return value;
	}

	public ContextQueryer() {
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Object query(Node node, Map<String, Object> context) {
		return grep(context.get(value));
	}

}
