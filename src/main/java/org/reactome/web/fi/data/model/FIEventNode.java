package org.reactome.web.fi.data.model;

import org.reactome.web.diagram.data.graph.raw.EventNode;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * 
 * @author brunsont
 *
 */
public interface FIEventNode extends EventNode {

	@PropertyName("reactomeId")
	Long getDbId();
	
	@PropertyName("sourceType")
	String getSchemaClass();
	
}
