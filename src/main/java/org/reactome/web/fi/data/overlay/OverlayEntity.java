package org.reactome.web.fi.data.overlay;

/**
 * 
 * @author brunsont
 *
 */
public interface OverlayEntity {

	String getIdentifier();
	
	String getGeneName();
	
	String getEType();
	
	String getTissue();
	
	String getValue();
	
	Double getNumberValue();
}
