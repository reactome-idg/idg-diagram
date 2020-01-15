package org.reactome.web.fi.data.overlay.model.pairwise;

import java.util.List;

/**
 * 
 * @author brunsont
 *
 */
public class PairwiseOverlayProperties {

	private List<PairwiseOverlayObject> pairwiseOverlayObjects;
	private String geneNames;
	
	public PairwiseOverlayProperties(List<PairwiseOverlayObject> pairwiseOverlayObjects) {
		this.pairwiseOverlayObjects = pairwiseOverlayObjects;
	}

	public List<PairwiseOverlayObject> getPairwiseOverlayObjects() {
		return pairwiseOverlayObjects;
	}

	public void setPairwiseOverlayObjects(List<PairwiseOverlayObject> pairwiseOverlayObjects) {
		this.pairwiseOverlayObjects = pairwiseOverlayObjects;
	}

	public String getGeneNames() {
		return geneNames;
	}

	public void setGeneNames(String uniprots) {
		this.geneNames = uniprots;
	}
	
}
