package org.reactome.web.fi.messages;

import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.messages.MessagesPanel;
import org.reactome.web.fi.events.NoFIsAvailableEvent;
import org.reactome.web.fi.handlers.NoFIsAvailableHandler;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;

public class NoFIsAvailableMessage extends MessagesPanel implements NoFIsAvailableHandler{

	private InlineLabel msgTitle;
	
	public NoFIsAvailableMessage(EventBus eventBus) {
		super(eventBus);
		
		addStyleName(RESOURCES.getCSS().errorMessage());
		this.getElement().getStyle().setBackgroundColor("rgb(27, 135, 197)");
		this.getElement().getStyle().setBorderColor("rgb(42, 147, 189)");
		this.getElement().getStyle().setWidth(320	, Unit.PX);
		
		FlowPanel fp = new FlowPanel();
		fp.add(new PwpButton("close", RESOURCES.getCSS().close(), e -> onCloseClicked()));
		
		msgTitle = new InlineLabel("No functional interactions available.");
		msgTitle.setStyleName(RESOURCES.getCSS().errorMessageTitle());

		FlowPanel textSpace = new FlowPanel();
		textSpace.setStyleName(RESOURCES.getCSS().errorMessageText());
		textSpace.add(msgTitle);
		fp.add(textSpace);
		
		this.add(fp);
		setVisible(false);
		eventBus.addHandler(NoFIsAvailableEvent.TYPE, this);
	}
	
	private void onCloseClicked() {
		this.setVisible(false);
	}

	@Override
	public void onNoFIsAvailable(NoFIsAvailableEvent event) {
		this.setVisible(true);
	}
	
}
