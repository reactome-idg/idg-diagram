package org.reactome.web.fi.data.overlay;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

public interface RawOverlayEntity {

	@PropertyName("uniprot")
	String getIdentifier();
	
	@PropertyName("sym")
	String getGeneName();
	
	@PropertyName("targetDevLevel")
	String getDataValue();
	
}
