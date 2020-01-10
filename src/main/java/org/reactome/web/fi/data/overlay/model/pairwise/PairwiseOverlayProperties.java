package org.reactome.web.fi.data.overlay.model.pairwise;

import java.util.Collection;

/**
 * 
 * @author brunsont
 *
 */
public class PairwiseOverlayProperties {

	private Collection<PairwiseOverlayObject> pairwiseOverlayObjects;
	private String uniprots;
	
	public PairwiseOverlayProperties(Collection<PairwiseOverlayObject> pairwiseOverlayObjects) {
		this.pairwiseOverlayObjects = pairwiseOverlayObjects;
	}

	public Collection<PairwiseOverlayObject> getPairwiseOverlayObjects() {
		return pairwiseOverlayObjects;
	}

	public void setPairwiseOverlayObjects(Collection<PairwiseOverlayObject> pairwiseOverlayObjects) {
		this.pairwiseOverlayObjects = pairwiseOverlayObjects;
	}

	public String getUniprots() {
		return uniprots;
	}

	public void setUniprots(String uniprots) {
		this.uniprots = uniprots;
	}
	
}
