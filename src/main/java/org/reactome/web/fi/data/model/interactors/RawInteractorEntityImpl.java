package org.reactome.web.fi.data.model.interactors;

import java.util.List;

import org.reactome.web.diagram.data.interactors.raw.RawInteractor;
import org.reactome.web.diagram.data.interactors.raw.RawInteractorEntity;

/**
 * 
 * @author brunsont
 *
 */
public class RawInteractorEntityImpl implements RawInteractorEntity{

	private String acc; 						//uniprot acc of diagram source
	private int count;							//total count of interactions
	private List<RawInteractor> interactors;	//list of all interactors
	
	public RawInteractorEntityImpl(String acc, int count, List<RawInteractor> interactors) {
		this.acc = acc;
		this.count = count;
		this.interactors = interactors;
	}
	
	@Override
	public String getAcc() {
		return acc;
	}

	@Override
	public Integer getCount() {
		return count;
	}

	@Override
	public List<RawInteractor> getInteractors() {
		return interactors;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
}
