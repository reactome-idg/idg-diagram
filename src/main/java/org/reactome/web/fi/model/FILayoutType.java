package org.reactome.web.fi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author brunsont
 *
 */
public enum FILayoutType {

	RANDOM,
	GRID,
	CIRCLE,
	FORCE_DIRECTED;
	
	public static FILayoutType getType(String type) {
		if(type==null) return FORCE_DIRECTED;
		for(FILayoutType t: values()) {
			if(t.toString().toLowerCase().equals(type.toLowerCase()))
				return t;
		}
		return FORCE_DIRECTED;
	}
	
	public static List<String> getLayouts(){
		List<String> result = new ArrayList<>();
		for(FILayoutType t: values()) {
			result.add(t.toString().toLowerCase());
		}
		
		return result;
	}
}
