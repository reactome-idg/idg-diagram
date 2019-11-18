package org.reactome.web.fi.data.overlay;

import java.util.List;

/**
 * 
 * @author brunsont
 *
 */
public interface OverlayEntities {
    
    public String getDataType(); // e.g. target_dev_level, tissue_expression
    
    public String getValueType(); // e.g. String (for enum)
    
    public String getDiscrete();
    
	List<TargetLevelEntity> getTargetLevelEntity();
	
	List<ExpressionEntity> getExpressionEntity();
	
}
