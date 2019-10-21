package org.reactome.web.fi.data.overlay;

public class OverlayResource {
	
	public enum OverlayType{
		PROTEINTARGETLEVEL
	}
	
	private OverlayType type;
	
	public OverlayResource(OverlayType type) {
		this.type = type;
	}
	
	public OverlayType getOverlyType() {
		return this.type;
	}
}
