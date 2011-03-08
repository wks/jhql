package org.github.wks.jhql.query;

import java.util.Map;

import org.github.wks.jhql.query.annotation.Required;
import org.w3c.dom.Node;

public class LiteralQueryer implements Queryer {
	private String value;

	public String getValue() {
		return value;
	}

	@Required
	public void setValue(String value) {
		this.value = value;
	}

	public LiteralQueryer() {
		super();
	}

	public LiteralQueryer(String value) {
		super();
		this.value = value;
	}

	public Object query(Node node, Map<String, Object> context) {
		return value;
	}

}
