package org.reactome.web.fi.data.layout;

import org.reactome.web.diagram.data.layout.Shape;

/**
 * 
 * @author brunsont
 *
 */
public interface DrugTargetItem {
	
	String getType();
	
	Shape getShape();
	
	Boolean getPressed();
	
	void setPressed(Boolean pressed);
	
	Integer getNumber();
	
	void setNumber(Integer number);
	
	void setHit(Boolean hit);
	
	Boolean getHit();
	
}
