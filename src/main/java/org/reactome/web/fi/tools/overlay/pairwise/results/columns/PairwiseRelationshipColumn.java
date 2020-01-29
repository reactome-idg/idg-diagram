package org.reactome.web.fi.tools.overlay.pairwise.results.columns;

import org.reactome.web.fi.tools.overlay.pairwise.PairwiseTableEntity;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;

public class PairwiseRelationshipColumn extends Column<PairwiseTableEntity, String> {

	public PairwiseRelationshipColumn() {
		super(new TextCell());
	}

	@Override
	public String getValue(PairwiseTableEntity object) {
		return object.getPosOrNeg();
	}

	
	
}
