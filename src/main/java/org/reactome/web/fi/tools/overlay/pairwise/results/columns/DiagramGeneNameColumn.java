package org.reactome.web.fi.tools.overlay.pairwise.results.columns;

import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseEntity;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;

public class DiagramGeneNameColumn extends Column<PairwiseEntity, String> {

	private static final String EXPLAINATION = "The reactome diagram source for the interaction";
	
	public DiagramGeneNameColumn(Cell<String> cell) {
		super(new TextCell());
		
	}

	@Override
	public String getValue(PairwiseEntity object) {
		return object.getGene();
	}
	
	

}
