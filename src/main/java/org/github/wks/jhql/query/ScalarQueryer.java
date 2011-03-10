package org.github.wks.jhql.query;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Node;

public abstract class ScalarQueryer<T> implements Queryer {
	Pattern grepPattern = null;

	public void setGrep(String pattern) {
		this.grepPattern = Pattern.compile(pattern);
		if (this.grepPattern.matcher("").groupCount() != 1) {
			throw new IllegalArgumentException(
					"The 'grep' property of a ScalarQueryer must have exactly 1 capturing group.");
		}
	}
	
	public T query(Node node, Map<String,Object> context) {
		return convert(grep(generate(node, context)), context);
	};

	protected abstract Object generate(Node node, Map<String,Object> context);

	protected final Object grep(Object obj) {
		if (grepPattern != null) {
			Matcher m = grepPattern.matcher(obj.toString());
			if (m.find()) {
				return m.group(1);
			} else {
				return "";
			}
		} else {
			return obj;
		}
	}
	
	@SuppressWarnings("unchecked")
	protected T convert(Object object, Map<String,Object> context) {
		return (T) object;
	}
}
