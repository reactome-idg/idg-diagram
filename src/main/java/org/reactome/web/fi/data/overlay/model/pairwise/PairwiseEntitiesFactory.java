package org.reactome.web.fi.data.overlay.model.pairwise;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * 
 * @author brunsont
 *
 */
public class PairwiseEntitiesFactory {

	interface ModelAutoBeanFactory extends AutoBeanFactory{
		AutoBean<PairwiseEntities> pairwiseEntities();
		AutoBean<PairwiseEntity> pairwiseEntity();
		AutoBean<DataDesc> dataDesc();
	}
	
	public static <T> T getPairwiseEntities(Class<T> cls, String json) throws Exception{
		try {
			AutoBeanFactory factory = GWT.create(ModelAutoBeanFactory.class);
			AutoBean<T> bean = AutoBeanCodex.decode(factory, cls, json);
			return bean.as();
		}catch(Throwable e) {
			throw new Exception("AutoBean could not be generated for pairwise entities.");
		}
	}
	
}
