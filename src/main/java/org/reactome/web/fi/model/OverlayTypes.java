package org.reactome.web.fi.model;

import java.util.ArrayList;
import java.util.List;

public enum OverlayTypes {

	TARGET_DEV_LEVEL;
	
	public static OverlayTypes getType(String type) {
		if(type == null) return null;
		for(OverlayTypes t: values()) {
			if(t.toString().toLowerCase().equals(type.toLowerCase()))
				return t;
		}
		return null;
	}
	
	public static List<String> getTypes(){
		List<String> result = new ArrayList<>();
		for(OverlayTypes t: values()) {
			result.add(t.toString().toLowerCase());
		}
		return result;
	}
}
