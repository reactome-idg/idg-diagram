package org.reactome.web.fi.data.tcrd.tagetlevel;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

public interface RawTargetLevelEntity {

	@PropertyName("uniprot")
	String getIdentifier();
	
	@PropertyName("sym")
	String getGeneName();
	
	@PropertyName("targetDevLevel")
	String getTargetDevLevel();
	
}
