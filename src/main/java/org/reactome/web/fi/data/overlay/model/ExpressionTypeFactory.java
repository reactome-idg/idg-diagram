package org.reactome.web.fi.data.overlay.model;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;


/**
 * 
 * @author brunsont
 *
 */
public class ExpressionTypeFactory {

	interface ModelAutoBeanFactory extends AutoBeanFactory{
		AutoBean<ExpressionTypeEntities> expressionTypeEntities();
		AutoBean<ExpressionTypeEntity> expressionTypeEntity();
	}
	
	public static <T> T getExpressionTypeEntities(Class<T> cls, String json) throws Exception {
		try {
			AutoBeanFactory factory = GWT.create(ModelAutoBeanFactory.class);
			AutoBean<T> bean = AutoBeanCodex.decode(factory, cls, json);
			return bean.as();
		}catch(Throwable e) {
			throw new Exception("Autobean could not be generated");
		}
	}
	
}
