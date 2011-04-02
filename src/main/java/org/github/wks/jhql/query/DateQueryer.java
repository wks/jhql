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