package org.reactome.web.fi.tools.overlay.pairwise.results.columns;

import org.reactome.web.fi.tools.overlay.pairwise.PairwiseTableEntity;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;

/**
 * 
 * @author brunsont
 *
 */
public class PairwiseSourceColumn extends Column<PairwiseTableEntity, String> {

	public PairwiseSourceColumn() {
		super(new TextCell());
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getValue(PairwiseTableEntity object) {
		return object.getDataDesc();
	}

}
