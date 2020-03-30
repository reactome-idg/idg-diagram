package org.reactome.web.fi.tools.overlay.pairwise;

import java.util.List;

import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayObject;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * 
 * @author brunsont
 *
 */
public class PairwisePopupTablePanel extends FlowPanel{

	public interface PairwiseTableHandler {
		
	}
	
	private NewPairwisePopup.Resources RESOURCES;
	private List<PairwiseOverlayObject> pairwiseOverlayProperties;
	
	public PairwisePopupTablePanel(List<PairwiseOverlayObject> pairwiseOverlayProperties, NewPairwisePopup.Resources RESOURCES) {
		this.RESOURCES = RESOURCES;
		this.pairwiseOverlayProperties = pairwiseOverlayProperties;
		
		initPanel();
	}

	private void initPanel() {
		Button infoButton = new Button("Show/Hide info");
		infoButton.setStyleName(RESOURCES.getCSS().infoButton());
		infoButton.addClickHandler(e -> onInfoButtonClicked());
		this.add(infoButton);
	}

	private void onInfoButtonClicked() {
		// TODO: show or hide future PairwisePopupTablePanel
	}
	
}
