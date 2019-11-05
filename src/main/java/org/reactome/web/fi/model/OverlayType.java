package org.reactome.web.fi.model;

import java.util.ArrayList;
import java.util.List;

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
	
	OverlayType(String overlayString){
		this.name = overlayString;
	}
	
	public String getName() {
		return this.name;
	}
	
	public static OverlayType getType(String type) {
		if(type == null) return null;
		for(OverlayType t: values()) {
			if(t.toString().toLowerCase().equals(type.toLowerCase()))
				return t;
		}
		return null;
	}
	
	public static List<String> getTypes(){
		List<String> result = new ArrayList<>();
		for(OverlayType t: values()) {
			result.add(t.toString().toLowerCase());
		}
		return result;
	}
}
