package org.reactome.web.fi.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author brunsont
 *
 */
public enum OverlayDataType {

	TARGET_DEVELOPMENT_LEVEL("Target Development Level","targetlevel/uniprots" ),
	TISSUE_EXPRESSION("Tissue Expression", "expressions/uniprots");
	
	private String name;
	private String url;
	private static final Map<String, OverlayDataType> LOOKUP = new HashMap<>();
	
	static {
		for(OverlayDataType type: OverlayDataType.values())
			LOOKUP.put(type.getName(), type);
	}
	
	OverlayDataType(String overlayString, String url){
		this.name = overlayString;
		this.url = url;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public static OverlayDataType lookupType(String type) {
		return LOOKUP.get(type);
	}
	
}
