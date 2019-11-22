package org.reactome.web.fi.data.overlay.model;

import java.util.List;

/**
 * 
 * @author brunsont
 *
 */
public interface OverlayEntities {
    
    public String getDataType(); // e.g. target_dev_level, tissue_expression
    
    public String getValueType(); // e.g. String (for enum)
        
	List<TargetLevelEntity> getTargetLevelEntity();
	
	List<ExpressionEntity> getExpressionEntity();
	
}
