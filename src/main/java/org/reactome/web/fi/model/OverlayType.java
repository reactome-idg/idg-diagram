package org.reactome.web.fi.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author brunsont
 *
 */
public enum OverlayType {

	TARGET_DEVELOPMENT_LEVEL("Target Development Level"),
	TISSUE_mRNA_EXPRESSION("Tissue mRNA Expression"),
	TISSUE_PROTEIN_EXPRESSION("Tissue Protein Expression");
	
	private String name;
	private static final Map<String, OverlayType> LOOKUP = new HashMap<>();
	
	static {
		for(OverlayType type: OverlayType.values())
			LOOKUP.put(type.getName(), type);
	}
	
	OverlayType(String overlayString){
		this.name = overlayString;
	}
	
	public String getName() {
		return this.name;
	}
	
	public static OverlayType lookupType(String type) {
		return LOOKUP.get(type);
	}
	
	public static List<String> getTypes(){
		List<String> result = new ArrayList<>();
		for(OverlayType t: OverlayType.values()) {
			result.add(t.getName());
		}
		return result;
	}
}
