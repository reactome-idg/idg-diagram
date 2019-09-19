package org.reactome.web.fi.data.model;

import java.util.List;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * 
 * @author brunsont
 *
 */
public interface SourcesEntity {
	
	@PropertyName("reactomeId")
	Long getDbId();
	
	@PropertyName("sourceType")
	String getSchemaClass();
}
