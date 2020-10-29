package kr.tracom.smps.common;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.asynchttpclient.Param;

public class Util {
	public static void delay(double delayTime) {
		long saveTime = System.currentTimeMillis();
		long curTime = 0;
		
		while(curTime - saveTime < delayTime) {
			curTime = System.currentTimeMillis();
		}
	}
	
	public static <T> Predicate<T> distincByKey(Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> map = new HashMap<>();
		return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	
	public static Map<String, List<String>> splitQuery(String param) {
	    return Arrays.stream(param.split("&"))
	            .map(it -> {
	            	final int idx = it.indexOf("=");
	        	    final String key = idx > 0 ? it.substring(0, idx) : it;
	        	    final String value = idx > 0 && it.length() > idx + 1 ? it.substring(idx + 1) : null;
	        	    return new SimpleImmutableEntry<>(key, value);
	            })
	            .collect(Collectors.groupingBy(SimpleImmutableEntry::getKey, LinkedHashMap::new, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
	}
	
	public static List<Param> splitQueryToList(String param) {
		List<Param> list = new ArrayList<>();

		String[] params = param.split("&");
		
		for(int i = 0; i < params.length; i++) {
			final int idx = params[i].indexOf("=");
    	    final String key = idx > 0 ? params[i].substring(0, idx) : params[i];
    	    final String value = idx > 0 && params[i].length() > idx + 1 ? params[i].substring(idx + 1) : null;
    	    Param p = new Param(key, value);
    	    list.add(p);
		}
		
		return list;
	}
	
	public static Map<String, String> splitQueryToMap(String param) {
		Map<String, String> map = new HashMap<>();

		String[] params = param.split("&");
		
		for(int i = 0; i < params.length; i++) {
			final int idx = params[i].indexOf("=");
    	    final String key = idx > 0 ? params[i].substring(0, idx) : params[i];
    	    final String value = idx > 0 && params[i].length() > idx + 1 ? params[i].substring(idx + 1) : null;
    	    map.put(key, value);
		}
		
		return map;
	}
}