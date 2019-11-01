package org.reactome.web.fi.data.overlay;

import java.util.List;

/**
 * 
 * @author brunsont
 *
 */
public interface OverlayEntities {
    
    public String getDataType(); // e.g. target_dev_level
    
    public String getValueType(); // e.g. String (for enum)
    
	List<OverlayEntity> getEntities();
	
}
