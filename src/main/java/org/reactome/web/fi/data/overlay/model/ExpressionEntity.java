package org.reactome.web.fi.data.overlay.model;

public interface ExpressionEntity extends OverlayEntity{

	String getEtype();
	
	String getTissue();
	
	String getQualValue();
	
	Double getNumberValue();
	
	Boolean getBooleanValue();
	
	String getStringValue();
	
}
