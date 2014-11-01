package com.leec.tools.common;

import java.util.Comparator;
import java.util.Map;

public class MapComparator<T extends Map<String, Object>> implements Comparator<T> {
	private String[] groupKeys;
	private boolean nullIsFirst = true;
	
	public MapComparator(String[] groupKeys) {
		this.groupKeys = groupKeys;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int compare(T map1, T map2) {
		
		for (String groupKey : groupKeys) {
			Object o1 = map1.get(groupKey);
			Object o2 = map2.get(groupKey);
			
			if (o1 == null && o2 == null) {
				
			} else if (o1 == null) {
				return nullIsFirst ? 1 : -1;
			} else if (o2 == null) {
				return nullIsFirst ? -1 : 1;
			}
			
			if(o1 instanceof Comparable) {
				int c = ((Comparable) o1).compareTo((Comparable) o2);
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