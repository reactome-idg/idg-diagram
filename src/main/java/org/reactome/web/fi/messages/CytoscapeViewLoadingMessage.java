package org.reactome.web.fi.messages;

import org.reactome.web.diagram.messages.MessagesPanel;
import org.reactome.web.fi.events.FIViewMessageEvent;
import org.reactome.web.fi.handlers.FIViewMessageHandler;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;

public class CytoscapeViewLoadingMessage extends MessagesPanel implements FIViewMessageHandler{

	public CytoscapeViewLoadingMessage(EventBus eventBus) {
		super(eventBus);
		
		MessagesPanelCSS css = RESOURCES.getCSS();
		addStyleName(css.loadingMessage());
		
		this.setVisible(false);
		this.getElement().getStyle().setWidth(320, Unit.PX);
		InlineLabel lbl = new InlineLabel("Converting to Functional Interactions...");
		
		FlowPanel fp = new FlowPanel();
		fp.add(new Image(RESOURCES.loader()));
		fp.add(lbl);
		this.add(fp);
		
		this.initHandlers();
	}

	private void initHandlers() {
		eventBus.addHandler(FIViewMessageEvent.TYPE, this);
	}

	@Override
	public void onFIViewMessage(FIViewMessageEvent event) {
		if(event.getShowMessage())
			this.setVisible(true);
		else
			this.setVisible(false);
	}
}
