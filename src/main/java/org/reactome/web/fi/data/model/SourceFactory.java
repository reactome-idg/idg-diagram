package org.reactome.web.fi.data.model;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import org.reactome.web.diagram.data.layout.factory.DiagramObjectException;


/**
 * 
 * @author brunsont
 *
 */
public class SourceFactory {

	interface ModelAutoBeanFactory extends AutoBeanFactory{
		AutoBean<SourcesEntity> sourcesEntity();
	}
	
	public static <T> T getSourceEntity(Class<T> cls, String json) throws DiagramObjectException{
		try {
			AutoBeanFactory factory = GWT.create(ModelAutoBeanFactory.class);
			AutoBean<T> bean = AutoBeanCodex.decode(factory, cls, json);
			return bean.as();
		} catch(Throwable e) {
			throw new DiagramObjectException("Error mapping json string for [" +  cls + "]", e);
		}
	}
	
}
