package org.github.wks.jhql.query;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.github.wks.jhql.query.annotation.Required;
import org.w3c.dom.Node;

/**
 * A Queryer that merges multiple "list" querys, resulting a list of objects
 * with each key-value pair from each list.
 * 
 * @author wks
 * 
 */
public class ZipQueryer implements Queryer {
	private static enum Alignment {
		shortest, longest
	}

	private Queryer from = null;

	public Queryer getFrom() {
		return from;
	}

	@Required
	public void setFrom(Queryer from) {
		this.from = from;
	}

	private Alignment alignTo = Alignment.shortest;

	public String getAlignTo() {
		return alignTo.toString();
	}
	
	public void setAlignTo(String alignTo) {
		this.alignTo = Alignment.valueOf(alignTo);
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> query(Node node,
			Map<String, Object> context) {
		Object fromResult = from.query(node, context);
		Map<String, List<Object>> fromResultAsMap;
		try {
			fromResultAsMap = (Map<String, List<Object>>) fromResult;
		} catch (ClassCastException e) {
			throw new QueryerRunningException(
					"The result of the 'from' subQueryer of a 'zip' queryer "
							+ "must be a Map<String, List<Object>>.", e);
		}

		List<Map<String, Object>> zippedResults = new ArrayList<Map<String, Object>>();

		int expectedSize;
		try {
			if (alignTo == Alignment.longest) {
				expectedSize = Integer.MIN_VALUE;
				for (List<Object> list : fromResultAsMap.values()) {
					expectedSize = Math.max(expectedSize, list.size());
				}
			} else {
				expectedSize = Integer.MAX_VALUE;
				for (List<Object> list : fromResultAsMap.values()) {
					expectedSize = Math.min(expectedSize, list.size());
				}
			}
		} catch (ClassCastException e) {
			throw new QueryerRunningException(
					"The result of the 'from' subQueryer of a 'zip' queryer "
							+ "must be a Map<String, List<Object>>.", e);
		}

		for (int i = 0; i < expectedSize; i++) {
			Map<String, Object> item = new LinkedHashMap<String, Object>();
			for (Map.Entry<String, List<Object>> e : fromResultAsMap.entrySet()) {
				String key = e.getKey();
				List<Object> list = e.getValue();
				Object listItem = null;
				if (i < list.size()) {
					listItem = list.get(i);
				}

				item.put(key, listItem);
			}

			zippedResults.add(item);
		}

		return zippedResults;
	}
}
