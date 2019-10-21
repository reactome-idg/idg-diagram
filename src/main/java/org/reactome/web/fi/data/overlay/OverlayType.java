package org.reactome.web.fi.data.overlay;

public class OverlayType {

	public enum OverlayTypes{
		PROTEINTARGETLEVEL
	}
	
	private OverlayTypes type;
	
	public OverlayType(OverlayTypes type) {
		this.type = type;
	}
	
	public OverlayTypes getOverlyType() {
		return this.type;
	}
}
