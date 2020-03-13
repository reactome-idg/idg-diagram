package org.reactome.web.fi.model;

/**
 * 
 * @author brunsont
 *
 */
public enum OverlayEntityType {

	TEST_ENTITY_OVERLAY("Test Entity Overlay");
	
	private String name;
	
	OverlayEntityType(String entityString){
		this.name = entityString;
	}
	
	public String getName() {
		return this.name;
	}
	
	public static OverlayEntityType lookupType(String type) {
		String look = type.replace(" ", "_").toUpperCase();
		return valueOf(look);
	}
	
}
