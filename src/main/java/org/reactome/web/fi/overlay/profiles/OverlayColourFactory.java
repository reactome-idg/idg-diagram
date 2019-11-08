package org.reactome.web.fi.overlay.profiles;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * 
 * @author brunsont
 *
 */
public class OverlayColourFactory {

	interface ModelAutoBeanFactory extends AutoBeanFactory{
		AutoBean<OverlayColourProperties> overlayColourProperties();
		AutoBean<OverlayColourNode> overlayColourNode();
	}
	
	public static <T> T getOverlayObject(Class<T> cls, String json) throws Exception{
		try {
			AutoBeanFactory factory = GWT.create(ModelAutoBeanFactory.class);
			AutoBean<T> bean = AutoBeanCodex.decode(factory, cls, json);
			return bean.as();
		} catch(Throwable e) {
			throw new Exception("Couldn't create Overlay Color Profile Object for object: " + json);
		}
	}
	
}
