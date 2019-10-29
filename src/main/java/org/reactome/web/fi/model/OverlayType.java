package org.reactome.web.fi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author brunsont
 *
 */
public enum OverlayType {

	TARGET_DEV_LEVEL;
	
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
