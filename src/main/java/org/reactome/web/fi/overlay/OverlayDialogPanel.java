package org.reactome.web.fi.overlay;

import org.reactome.web.diagram.common.PwpButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

public class OverlayDialogPanel extends DialogBox implements ClickHandler{

	private EventBus eventBus;
	private Button close;
	
	public OverlayDialogPanel(EventBus eventBus) {
		this.eventBus = eventBus;
		
		setAutoHideEnabled(false);
		setModal(false);
		setStyleName(org.reactome.web.diagram.context
					 .ContextDialogPanel.RESOURCES.getCSS().popup());
		
		FlowPanel fp = new FlowPanel();
		fp.add(this.close = new PwpButton("Close",
										  org.reactome.web.diagram.context
										  .ContextDialogPanel.RESOURCES.getCSS()
										  .close(),
										  this));
		fp.add(new OverlayInfoPanel(eventBus));
		setTitlePanel();
		setWidget(fp);
		this.addStyleName(org.reactome.web.diagram.context
						  .ContextDialogPanel.RESOURCES.getCSS()
						  .popupSelected());
		
		show();

	}

	private void setTitlePanel() {
		InlineLabel title = new InlineLabel("Overlay Resources");
		SafeHtml safeHtml = SafeHtmlUtils.fromTrustedString(title.toString());
		getCaption().setHTML(safeHtml);
		getCaption().asWidget().setStyleName(org.reactome.web.diagram.context
											 .ContextDialogPanel.RESOURCES.getCSS()
											 .header());
	}

	//position set in IdgViewerContainer
	@SuppressWarnings("unused")
	private void setPosition(int x, int y) {
		setPopupPosition(x,y);
		
	}
	
	public void show() {
		super.show();
	}
	
	public void hide() {
		super.hide();
	}

	@Override
	public void onClick(ClickEvent event) {
		Button btn = (Button) event.getSource();
		if(btn.equals(close)) {
			hide();
		}
	}
	
}
