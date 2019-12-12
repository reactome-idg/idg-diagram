package org.reactome.web.fi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author brunsont
 *
 */
public enum FILayoutType {

	COSE("Force Directed"),
	GRID("Grid"),
	CIRCLE("Circle"),
	RANDOM("Random");
	
	private String name;
	
	FILayoutType(String name){
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public static FILayoutType lookupType(String type) {
		String look = type.replace(" ", "_").toUpperCase();
		return valueOf(look);
	}
	
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
