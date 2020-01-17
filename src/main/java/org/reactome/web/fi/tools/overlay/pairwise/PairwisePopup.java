package org.reactome.web.fi.tools.overlay.pairwise;

import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.fi.tools.overlay.pairwise.factory.PairwisePopupFactory;

public class PairwisePopup extends AbstractPairwisePopup {

	private String popupId;
	
	public PairwisePopup(GraphObject graphObject) {
		this.popupId = graphObject.getStId();
	}

	public PairwisePopup(String uniprot, String geneName) {
		this.popupId = uniprot;
	}

	@Override
	public void hide() {
		PairwisePopupFactory.get().removePopup(this.popupId);
		super.hide();
	}
	
}
