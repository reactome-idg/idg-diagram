package org.reactome.web.fi.client.visualisers.fiview;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * 
 * @author brunsont
 *
 */
public class FIViewInfoPopup extends PopupPanel{

	private HTML htmlLabel;
	
	public FIViewInfoPopup() {
		super(true);
	}
	
	public HTML getHtmlLabel() {
		return htmlLabel;
	}

	public void setHtmlLabel(HTML htmlLabel) {
		this.htmlLabel = htmlLabel;
		this.setWidget(htmlLabel);
	}
	
}
