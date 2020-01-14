package org.reactome.web.fi.data.overlay.model.pairwise;

import java.util.List;

/**
 * 
 * @author brunsont
 *
 */
public interface PairwiseEntity {

	String getGene();
	
	DataDesc getDataDesc();
	
	List<String> getPosGenes();
	
	List<String> getNegGenes();
	
}
