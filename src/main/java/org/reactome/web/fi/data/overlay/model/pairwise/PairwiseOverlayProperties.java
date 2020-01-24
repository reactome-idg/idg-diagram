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
	
	public PairwiseOverlayProperties(List<PairwiseOverlayObject> pairwiseOverlayObjects, String uniprots) {
		this.pairwiseOverlayObjects = pairwiseOverlayObjects;
		this.uniprots = uniprots;
	}

	public List<PairwiseOverlayObject> getPairwiseOverlayObjects() {
		return pairwiseOverlayObjects;
	}

	public void setPairwiseOverlayObjects(List<PairwiseOverlayObject> pairwiseOverlayObjects) {
		this.pairwiseOverlayObjects = pairwiseOverlayObjects;
	}

	public String getGeneNames() {
		return uniprots;
	}

	public void setGeneNames(String uniprots) {
		this.uniprots = uniprots;
	}
	
}
