package org.github.wks.jhql.factory;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.github.wks.jhql.query.IntQueryer;
import org.github.wks.jhql.query.ListQueryer;
import org.github.wks.jhql.query.ObjectQueryer;
import org.github.wks.jhql.query.Queryer;
import org.github.wks.jhql.query.TextQueryer;

/**
 * A factory class that generates Queryer objects from JSON documents.
 */
public class JsonQueryerFactory {
	private static ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Make a Queryer from a File containing a JSON value.
	 */
	public static Queryer makeQueryer(File json) throws JsonException,
			JhqlJsonGrammarException {
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
			JhqlJsonGrammarException {
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
			JhqlJsonGrammarException {
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
			JhqlJsonGrammarException {
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
	 * @throws JhqlJsonGrammarException
	 */
	public static Queryer makeQueryer(Object queryExpr)
			throws JhqlJsonGrammarException {
		if (queryExpr instanceof String) {
			return makeSimpleQueryer((String) queryExpr);
		} else if (queryExpr instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> queryExprMap = (Map<String, Object>) queryExpr;
			if (queryExprMap.containsKey("_type")) {
				return makeComplexedQueryer(queryExprMap);
			} else {
				return makeObjectQueryer(queryExprMap);
			}
		}
		throw new JhqlJsonGrammarException("Illegal JHQL query expression:"
				+ queryExpr);
	}

	private static Queryer makeSimpleQueryer(String queryExpr)
			throws JhqlJsonGrammarException {
		String[] pair = queryExpr.split(":");
		if (pair.length != 2) {
			throw new JhqlJsonGrammarException(
					"Illegal JHQL string expression: " + queryExpr);
		}

		String type = pair[0];
		String value = pair[1];

		Map<String, Object> queryExprObj = new HashMap<String, Object>();
		queryExprObj.put("_type", type);
		queryExprObj.put("value", value);

		return makeComplexedQueryer(queryExprObj);
	}

	private static Queryer makeComplexedQueryer(Map<String, Object> queryExpr) {
		String type;
		try {
			type = (String) queryExpr.get("_type");
		} catch (ClassCastException e) {
			throw new JhqlJsonGrammarException(
					"'_type' field must be a string.");
		}
		if (type == null) {
			throw new JhqlJsonGrammarException(
					"Complexed queryers must contain '_type' field.");
		}

		if (type.equals("text")) {
			String value;
			try {
				value = (String) queryExpr.get("value");
			} catch (ClassCastException e) {
				throw new JhqlJsonGrammarException(
						"The 'value' field of a 'text' queryer must be a string.");
			}
			return makeTextQueryer(value);
		} else if (type.equals("int")) {
			String value;
			try {
				value = (String) queryExpr.get("value");
			} catch (ClassCastException e) {
				throw new JhqlJsonGrammarException(
						"The 'value' field of an 'int' queryer must be a string.");
			}
			return makeIntQueryer(value);
		} else if (type.equals("list")) {
			String from;
			try {
				from = (String) queryExpr.get("from");
			} catch (NullPointerException e) {
				throw new JhqlJsonGrammarException(
						"A 'list' queryer must have a 'from' field.");
			} catch (ClassCastException e) {
				throw new JhqlJsonGrammarException(
						"The 'from' field of a 'list' queryer must be a string.");
			}

			Object mapper;
			try {
				mapper = queryExpr.get("select");
			} catch (NullPointerException e) {
				throw new JhqlJsonGrammarException(
						"A 'list' queryer must have a 'select' field.");
			}

			return makeListQueryer(from, mapper);
		}
		throw new JhqlJsonGrammarException("Unsupported queryer type: " + type);
	}

	private static Queryer makeTextQueryer(String value) {
		return new TextQueryer(value);
	}

	private static Queryer makeIntQueryer(String value) {
		return new IntQueryer(value);
	}

	private static Queryer makeListQueryer(String from, Object mapper) {
		Queryer mapperQueryer = makeQueryer(mapper);
		return new ListQueryer(from, mapperQueryer);
	}

	private static Queryer makeObjectQueryer(Map<String, Object> queryExpr) {
		Map<String, Queryer> fieldRules = new HashMap<String, Queryer>();

		for (Map.Entry<String, Object> pair : queryExpr.entrySet()) {
			Queryer fieldQueryer = makeQueryer(pair.getValue());
			fieldRules.put(pair.getKey(), fieldQueryer);
		}
		return new ObjectQueryer(fieldRules);
	}
}
