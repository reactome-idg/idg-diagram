package org.reactome.web.fi.data.model.drug;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * 
 * @author brunsont
 *
 */
public class DrugTargetEntitiesFactory {

	interface ModelAutoBeanFactory extends AutoBeanFactory{ 
		AutoBean<DrugTargetEntities> drugTargetEntities();
		AutoBean<DrugTargetEntity> drugTargetEntity();
		AutoBean<Target> target();
		AutoBean<Protein> protein();
	}
	
	public static <T> T getDrugTargetEntities(Class<T> cls, String json) throws Exception {
		try {
			AutoBeanFactory factory = GWT.create(ModelAutoBeanFactory.class);
			AutoBean<T> bean = AutoBeanCodex.decode(factory, cls, json);
			return bean.as();
		}catch(Throwable e) {
			throw new Exception("Autobean could not be generated");
		}
	}
	
}
