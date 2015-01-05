package com.leec.tools.common;

import java.util.Comparator;
import java.util.Map;

public class MapComparator<T> implements Comparator<Map<String, T>> {
	private String[] orderKeys;
	private boolean nullIsFirst;
	
	public MapComparator(String[] orderKeys) {
		this(orderKeys, false);
	}

	public MapComparator(String[] orderKeys, boolean nullIsFirst) {
		this.orderKeys = orderKeys;
		this.nullIsFirst = nullIsFirst;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int compare(Map<String, T> map1, Map<String, T> map2) {
		
		for (String orderKey : orderKeys) {
			T o1 = map1.get(orderKey);
			T o2 = map2.get(orderKey);
			
			if (o1 == null && o2 == null) {
				return 0;
			} else if (o1 == null) {
				return nullIsFirst ? -1 : 1;
			} else if (o2 == null) {
				return nullIsFirst ? 1 : -1;
			}
			
			if(o1 instanceof Comparable) {
				int c = ((Comparable) o1).compareTo(o2);
				if (c != 0) {
					return c;
				}
			} else {
				throw new UnsupportedOperationException(o1.getClass().getName() + "类型不支持排序");
			}
		}
		
		return 0;
	}
}