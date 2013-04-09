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
package org.github.wks.jhql.factory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.github.wks.jhql.query.ContextQueryer;
import org.github.wks.jhql.query.DateQueryer;
import org.github.wks.jhql.query.IntQueryer;
import org.github.wks.jhql.query.ListQueryer;
import org.github.wks.jhql.query.LiteralQueryer;
import org.github.wks.jhql.query.ObjectQueryer;
import org.github.wks.jhql.query.Queryer;
import org.github.wks.jhql.query.SingleQueryer;
import org.github.wks.jhql.query.TextQueryer;
import org.github.wks.jhql.query.ZipQueryer;
import org.github.wks.jhql.query.annotation.Required;

/**
 * A factory class that generates Queryer objects from JSON documents.
 */
public class JsonQueryerFactory {
	private static Map<String, Class<? extends Queryer>> namedQueryers = new HashMap<String, Class<? extends Queryer>>();

	static {
		namedQueryers.put("text", TextQueryer.class);
		namedQueryers.put("int", IntQueryer.class);
		namedQueryers.put("single", SingleQueryer.class);
		namedQueryers.put("list", ListQueryer.class);
		namedQueryers.put("context", ContextQueryer.class);
		namedQueryers.put("date", DateQueryer.class);
		namedQueryers.put("literal", LiteralQueryer.class);
		namedQueryers.put("zip", ZipQueryer.class);
	}

	private static ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Make a Queryer from a File containing a JSON value.
	 */
	public static Queryer makeQueryer(File json) throws JsonException,
			JhqlGrammarException {
		Object queryExpr;
		try {
			queryExpr = objectMapper.readValue(json, Object.class);
		} catch (Exception e) {
			throw new JsonException(e);
		}
		return makeQueryer(queryExpr);
	}

	/**
	 * Make a Queryer from a Reader containing a JSON value.
	 */
	public static Queryer makeQueryer(Reader json) throws JsonException,
			JhqlGrammarException {
		Object queryExpr;
		try {
			queryExpr = objectMapper.readValue(json, Object.class);
		} catch (Exception e) {
			throw new JsonException(e);
		}
		return makeQueryer(queryExpr);
	}

	/**
	 * Make a Queryer from an InputStream containing a JSON value.
	 */
	public static Queryer makeQueryer(InputStream json) throws JsonException,
			JhqlGrammarException {
		Object queryExpr;
		try {
			queryExpr = objectMapper.readValue(json, Object.class);
		} catch (Exception e) {
			throw new JsonException(e);
		}
		return makeQueryer(queryExpr);
	}

	/**
	 * Make a Queryer from an String containing a JSON value.
	 */
	public static Queryer makeQueryer(String json) throws JsonException,
			JhqlGrammarException {
		Object queryExpr;
		try {
			queryExpr = objectMapper.readValue(json, Object.class);
		} catch (Exception e) {
			throw new JsonException(e);
		}
		return makeQueryer(queryExpr);
	}

	/**
	 * Make a Queryer from a mapped JSON object. JSON values are mapped to Java
	 * types like int, String, boolean, List, Map, etc.
	 * 
	 * @param json
	 *            The Java object corresponding to the JSON grammar.
	 * @return A Queryer object.
	 * @throws JhqlGrammarException
	 */
	public static Queryer makeQueryer(Object queryExpr)
			throws JhqlGrammarException {
		if (queryExpr instanceof String) {
			return makeSimpleQueryer((String) queryExpr);
		} else if (queryExpr instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> queryExprMap = (Map<String, Object>) queryExpr;
			if (queryExprMap.containsKey("_type")) {
				return makeComplexQueryer(queryExprMap);
			} else {
				return makeObjectQueryer(queryExprMap);
			}
		}
		throw new JhqlGrammarException("Illegal JHQL expression: " + queryExpr);
	}

	private static Queryer makeSimpleQueryer(String queryExpr)
			throws JhqlGrammarException {
		String[] pair = queryExpr.split(":");
		if (pair.length != 2) {
			throw new JhqlGrammarException("Illegal JHQL string expression: "
					+ queryExpr);
		}

		String type = pair[0];
		String value = pair[1];

		Map<String, Object> queryExprObj = new HashMap<String, Object>();
		queryExprObj.put("_type", type);
		queryExprObj.put("value", value);

		return makeComplexQueryer(queryExprObj);
	}

	private static Queryer makeComplexQueryer(Map<String, Object> queryExpr) {
		String type;
		try {
			type = (String) queryExpr.get("_type");
		} catch (ClassCastException e) {
			throw new JhqlGrammarException("'_type' field must be a string.");
		}
		if (type == null) {
			throw new JhqlGrammarException(
					"Complexed queryers must contain a '_type' field.");
		}

		Class<? extends Queryer> queryerClass = namedQueryers.get(type);

		if (queryerClass == null) {
			throw new JhqlGrammarException("Unsupported queryer type: '" + type
					+ "'.");
		}

		Queryer queryer;
		try {
			queryer = queryerClass.newInstance();
		} catch (Exception e) {
			throw new JhqlGrammarException("Cannot instantiate queryer '"
					+ type + "'.", e);
		}

		Map<String, Object> queryExprCopy = new HashMap<String, Object>(
				queryExpr);
		queryExprCopy.remove("_type");

		PropertyDescriptor[] propertyDescriptors;

		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(queryerClass);
			propertyDescriptors = beanInfo.getPropertyDescriptors();
		} catch (IntrospectionException e) {
			throw new JhqlGrammarException("Cannot introspect queryer '" + type
					+ "'.", e);
		}

		for (PropertyDescriptor pd : propertyDescriptors) {
			String propertyName = pd.getName();

			Method writeMethod = pd.getWriteMethod();
			if (writeMethod == null) {
				continue; // Property not writable.
			}

			Object exprValue = queryExpr.get(propertyName);

			if (exprValue != null) {
				Class<?> propertyType = pd.getPropertyType();

				Object valueToWrite;
				if (Queryer.class.isAssignableFrom(propertyType)) {
					valueToWrite = makeQueryer(exprValue);
				} else {
					valueToWrite = exprValue;
				}

				try {
					writeMethod.invoke(queryer, valueToWrite);
				} catch (Exception e) {
					throw new JhqlGrammarException("Cannot set property '"
							+ propertyName + "' on Queryer type '" + type
							+ "'.", e);
				}
				queryExprCopy.remove(propertyName);
			} else {
				Required requiredAnnotation = writeMethod
						.getAnnotation(Required.class);
				if (requiredAnnotation != null) {
					throw new JhqlGrammarException("Property '" + propertyName
							+ "' is required for Queryer type '" + type + "'.");
				}
			}
		}

		if (!queryExprCopy.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (String str : queryExprCopy.keySet()) {
				if (first) {
					first = false;
				} else {
					sb.append(',');
				}
				sb.append(str);
			}
			throw new JhqlGrammarException("Unexpected property '"
					+ sb.toString() + "' on Queryer type '" + type + "'.");
		}

		return queryer;
	}

	private static Queryer makeObjectQueryer(Map<String, Object> queryExpr) {
		Map<String, Queryer> fieldRules = new LinkedHashMap<String, Queryer>();

		for (Map.Entry<String, Object> pair : queryExpr.entrySet()) {
			Queryer fieldQueryer = makeQueryer(pair.getValue());
			fieldRules.put(pair.getKey(), fieldQueryer);
		}
		return new ObjectQueryer(fieldRules);
	}
}
