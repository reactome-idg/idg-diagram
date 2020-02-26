package org.reactome.web.fi.data.overlay.model.pairwise;

/**
 * 
 * @author brunsont
 *
 */
public interface PairwiseNumberEntity {

	String getGene();
	
	DataDesc getDataDesc();
	
	int getPosNum();
	
	int getNegNum();
	
}
