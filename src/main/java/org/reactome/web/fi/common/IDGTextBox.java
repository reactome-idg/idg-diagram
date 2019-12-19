package org.reactome.web.fi.common;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.TextBox;

public class IDGTextBox extends TextBox{

	public IDGTextBox() {
		super();
		sinkEvents(Event.ONPASTE);
	}

	@Override
	public void onBrowserEvent(Event event) {
		super.onBrowserEvent(event);
		switch(DOM.eventGetType(event)) {
			case Event.ONPASTE:
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {

					@Override
					public void execute() {
						ValueChangeEvent.fire(IDGTextBox.this, getText());
					}
					
				});
				break;
		}
	}
	
	
	
}
