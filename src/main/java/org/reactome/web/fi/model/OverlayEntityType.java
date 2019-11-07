package org.reactome.web.fi.model;

import java.util.HashMap;
import java.util.Map;

public enum OverlayEntityType {

	TEST_ENTITY_OVERLAY("Test Entity Overlay");
	
	
	private String name;
	private static final Map<String, OverlayEntityType> LOOKUP =  new HashMap<>();
	
	static {
		for(OverlayEntityType type: OverlayEntityType.values())
			LOOKUP.put(type.getName(), type);
	}
	
	OverlayEntityType(String entityString){
		this.name = entityString;
	}
	
	public String getName() {
		return this.name;
	}
	
	public static OverlayEntityType lookupType(String type) {
		return LOOKUP.get(type);
	}
	
	
}
