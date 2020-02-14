package org.reactome.web.fi.data.model.interactors;

import org.reactome.web.diagram.data.interactors.raw.RawResource;

/**
 * 
 * @author brunsont
 *
 */
public class RawResourceImpl implements RawResource{

	private String name;
	private boolean active;
	
	public RawResourceImpl(String name, boolean active) {
		this.name = name;
		this.active = active;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public Boolean getActive() {
		return active;
	}

}
