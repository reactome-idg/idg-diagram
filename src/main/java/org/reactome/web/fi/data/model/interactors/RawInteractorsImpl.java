package org.reactome.web.fi.data.model.interactors;

import java.util.List;

import org.reactome.web.diagram.data.interactors.raw.RawInteractorEntity;
import org.reactome.web.diagram.data.interactors.raw.RawInteractors;

/**
 * 
 * @author brunsont
 *
 */
public class RawInteractorsImpl implements RawInteractors{

	private String resource;
	private List<RawInteractorEntity> entities;
	
	public RawInteractorsImpl(String resource, List<RawInteractorEntity> entities) {
		this.resource = resource;
		this.entities = entities;
	}
	
	@Override
	public String getResource() {
		return resource;
	}

	@Override
	public List<RawInteractorEntity> getEntities() {
		return entities;
	}

}
