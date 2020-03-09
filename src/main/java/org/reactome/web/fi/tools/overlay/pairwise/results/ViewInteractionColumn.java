package org.reactome.web.fi.tools.overlay.pairwise.results;

import org.reactome.web.fi.tools.overlay.pairwise.model.PairwiseTableEntity;

import com.google.gwt.cell.client.ActionCell;

public class ViewInteractionColumn extends ActionCell<PairwiseTableEntity> {

	public ViewInteractionColumn(String text, Delegate<PairwiseTableEntity> delegate) {
		super(text, delegate);
		
	}

}
