package org.reactome.web.fi.data.overlay;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public abstract class OverlayEntityDataFactory {

	interface ModelAutoBeanFactory extends AutoBeanFactory{
		AutoBean<RawOverlayEntities> targetLevelEntities();
		AutoBean<RawOverlayEntity> targetLevelEntity();
	}
		
	public static <T> T getTargetLevelEntity(Class<T> cls, String json) throws Exception {
		try {
			AutoBeanFactory factory = GWT.create(ModelAutoBeanFactory.class);
			AutoBean<T> bean= AutoBeanCodex.decode(factory, cls, json);
			return bean.as();
		} catch(Throwable e) {
			throw new Exception();
		}
	}
	
	
}
