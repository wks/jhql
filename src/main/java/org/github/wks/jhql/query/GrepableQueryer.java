package org.github.wks.jhql.query;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class GrepableQueryer implements Queryer {
	Pattern grepPattern = null;

	public void setGrep(String pattern) {
		this.grepPattern = Pattern.compile(pattern);
		if (this.grepPattern.matcher("").groupCount() != 1) {
			throw new IllegalArgumentException(
					"The 'grep' property of a ScalarQueryer must have exactly 1 capturing group.");
		}
	}

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
}
