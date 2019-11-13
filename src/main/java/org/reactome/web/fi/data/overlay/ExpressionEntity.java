package org.reactome.web.fi.data.overlay;

public interface ExpressionEntity extends OverlayEntity{

	String getetype();
	
	String getTissue();
	
	String getQualValue();
	
	Double getNumberValue();
	
	Boolean getBooleanValue();
	
}
