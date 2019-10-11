package org.reactome.web.fi.model;

import java.util.ArrayList;
import java.util.List;

public enum FILayoutType {

	RANDOM,
	GRID,
	CIRCLE,
	COSE;
	
	public static FILayoutType getType(String type) {
		if(type==null) return COSE;
		for(FILayoutType t: values()) {
			if(t.toString().toLowerCase().equals(type.toLowerCase()))
				return t;
		}
		return COSE;
	}
	
	public static List<String> getLayouts(){
		List<String> result = new ArrayList<>();
		for(FILayoutType t: values()) {
			result.add(t.toString().toLowerCase());
		}
		
		return result;
	}
}
