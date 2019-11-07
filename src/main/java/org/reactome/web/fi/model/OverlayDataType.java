package org.reactome.web.fi.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author brunsont
 *
 */
public enum OverlayDataType {

	TARGET_DEVELOPMENT_LEVEL("Target Development Level"),
	TISSUE_mRNA_EXPRESSION("Tissue mRNA Expression"),
	TISSUE_PROTEIN_EXPRESSION("Tissue Protein Expression");
	
	private String name;
	private static final Map<String, OverlayDataType> LOOKUP = new HashMap<>();
	
	static {
		for(OverlayDataType type: OverlayDataType.values())
			LOOKUP.put(type.getName(), type);
	}
	
	OverlayDataType(String overlayString){
		this.name = overlayString;
	}
	
	public String getName() {
		return this.name;
	}
	
	public static OverlayDataType lookupType(String type) {
		return LOOKUP.get(type);
	}
	
}
