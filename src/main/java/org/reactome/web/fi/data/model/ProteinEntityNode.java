package org.reactome.web.fi.data.model;

import org.reactome.web.diagram.data.graph.raw.EntityNode;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * 
 * @author brunsont
 *
 */
public interface ProteinEntityNode extends EntityNode {

	@PropertyName("shortName")
	String getDisplayName();
	
	@PropertyName("identifier")
	String getIdentifier();
	
	@PropertyName("id")
	Long getDbId();
	
	@PropertyName("sourceType")
	String getSchemaClass();
	
}
