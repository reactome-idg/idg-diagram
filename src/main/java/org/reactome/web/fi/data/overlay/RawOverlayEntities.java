package org.reactome.web.fi.data.overlay;

import java.util.List;

/**
 * 
 * @author brunsont
 *
 */
public interface RawOverlayEntities {

	String getDataType();
	
	List<RawOverlayEntity> getEntities();
	
	
	void setDataType(String dataType);
}
