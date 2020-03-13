package org.reactome.web.fi.data.model;

import org.reactome.web.diagram.data.graph.raw.EntityNode;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * 
 * @author brunsont
 *
 */
public interface FIEntityNode extends EntityNode {
	
	@PropertyName("reactomeId")
	Long getDbId();
	
	@PropertyName("sourceType")
	String getSchemaClass();
	
}
