package me.metro.bgl.utils;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class Util {
	@SuppressLint("SimpleDateFormat")
	public static String get12Hora(String hora24) {
		SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm");
		SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mma");
		Date date;
		try {
			date = parseFormat.parse(hora24);
			return displayFormat.format(date);
		} catch (Exception e) {}
		return hora24;
	}
	public static String arrayToString(String[] a, String separator) {
	    if (a == null || separator == null) {
	        return null;
	    }
	    StringBuilder result = new StringBuilder();
	    if (a.length > 0) {
	        result.append(a[0]);
	        for (int i=1; i < a.length; i++) {
	            result.append(separator);
	            result.append(a[i]);
	        }
	    }
	    return result.toString();
	  }
	public static String setToString(Set<String> set, String separator) {
		if (set == null || separator == null) {
			return null;
		}
		StringBuilder result = new StringBuilder();
		Iterator<String> itr = set.iterator(); 
		if(itr.hasNext()){
			String element = itr.next();
			result.append(element);
		}
		while(itr.hasNext()) {
			String element = itr.next();
			result.append(separator);
            result.append(element);
		}
		 
		return result.toString();
	}
}
