package org.reactome.web.fi.model;

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
	
	public static FILayoutType getType(String type) {
		if(type==null) return COSE;
		for(FILayoutType t: values()) {
			if(t.getName().equals(type))
				return t;
		}
		return COSE;
	}
}
