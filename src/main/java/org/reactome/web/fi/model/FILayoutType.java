package org.reactome.web.fi.model;

public enum FILayoutType {

	NONE,
	RANDOM,
	GRID,
	CIRCLE,
	COSE;
	
	public FILayoutType getType(String type) {
		if(type==null) return NONE;
		for(FILayoutType t: values()) {
			if(t.toString().toLowerCase().equals(type.toLowerCase()))
				return t;
		}
		return NONE;
	}
	
}
