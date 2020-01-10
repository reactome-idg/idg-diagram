package org.reactome.web.fi.data.overlay.model.pairwise;

import java.util.List;

/**
 * 
 * @author brunsont
 *
 */
public class PairwiseOverlayProperties {

	private List<PairwiseOverlayObject> pairwiseOverlayObjects;
	private String uniprots;
	
	public PairwiseOverlayProperties(List<PairwiseOverlayObject> pairwiseOverlayObjects) {
		this.pairwiseOverlayObjects = pairwiseOverlayObjects;
	}

	public List<PairwiseOverlayObject> getPairwiseOverlayObjects() {
		return pairwiseOverlayObjects;
	}

	public void setPairwiseOverlayObjects(List<PairwiseOverlayObject> pairwiseOverlayObjects) {
		this.pairwiseOverlayObjects = pairwiseOverlayObjects;
	}

	public String getUniprots() {
		return uniprots;
	}

	public void setUniprots(String uniprots) {
		this.uniprots = uniprots;
	}
	
}
