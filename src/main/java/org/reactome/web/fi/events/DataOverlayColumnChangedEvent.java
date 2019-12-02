package org.reactome.web.fi.events;

import org.reactome.web.fi.handlers.DataOverlayColumnChangedHandler;

import com.google.gwt.event.shared.GwtEvent;

public class DataOverlayColumnChangedEvent extends GwtEvent<DataOverlayColumnChangedHandler>{
	public static Type<DataOverlayColumnChangedHandler> TYPE = new Type<>();
	
	int column;
	
	public DataOverlayColumnChangedEvent(int column) {
		this.column = column;
	}
	
	@Override
	public Type<DataOverlayColumnChangedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DataOverlayColumnChangedHandler handler) {
		handler.onDataOverlayColumnChanged(this);
	}

	public int getColumn() {
		return this.column;
	}
	
	@Override
	public String toString() {
		return "Data Overlay column changed to: " + column;
	}
	
}
