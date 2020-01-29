package org.reactome.web.fi.tools.overlay.pairwise.results.columns;

import org.reactome.web.fi.tools.overlay.pairwise.PairwiseTableEntity;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;

/**
 * 
 * @author brunsont
 *
 */
public class DiagramGeneNameColumn extends Column<PairwiseTableEntity, String> {

	private static final String EXPLAINATION = "The reactome diagram source for the interaction";
	
	public DiagramGeneNameColumn() {
		super(new TextCell());
		
	}

	@Override
	public String getValue(PairwiseTableEntity object) {
		return object.getDiagramSource();
	}
	
	

}
