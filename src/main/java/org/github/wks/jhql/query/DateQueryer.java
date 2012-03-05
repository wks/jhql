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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.github.wks.jhql.query.annotation.Required;

/**
 * A Queryer that returns a java.util.Date object.
 * <p>
 * It works like the TestQueryer, but converts the result into a Date. The
 * format of the date is set by the "dateFormat" property. If the result of
 * text querying is not an integer, it returns null.
 */
public class DateQueryer extends XPathQueryer<Date> {

	private String dateFormat;

	@Required
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public DateQueryer() {
	}

	public DateQueryer(String xPathExpression) throws IllegalArgumentException {
		this.setValue(xPathExpression);
	}

	public Date convert(Object obj, Map<String, Object> context) {
		String result = obj.toString();
		
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
		
		try {
			return sdf.parse(result);
		} catch (ParseException e) {
			return null;
		}
	
	}

}
