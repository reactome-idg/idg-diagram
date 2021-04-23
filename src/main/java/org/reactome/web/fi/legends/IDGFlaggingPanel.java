package org.reactome.web.fi.legends;

import java.util.ArrayList;
import java.util.List;

import org.reactome.web.diagram.events.DiagramObjectsFlagRequestedEvent;
import org.reactome.web.diagram.events.DiagramObjectsFlagResetEvent;
import org.reactome.web.diagram.events.DiagramObjectsFlaggedEvent;
import org.reactome.web.diagram.handlers.DiagramObjectsFlagRequestHandler;
import org.reactome.web.diagram.handlers.DiagramObjectsFlagResetHandler;
import org.reactome.web.diagram.handlers.DiagramObjectsFlaggedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class IDGFlaggingPanel extends AbsolutePanel implements HasMouseOverHandlers, 
	HasMouseOutHandlers, DiagramObjectsFlagResetHandler, DiagramObjectsFlagRequestHandler,
	DiagramObjectsFlaggedHandler{
	
	
	List<AbsolutePanel> panels;
	
	public IDGFlaggingPanel(EventBus eventBus) {
		
		this.setStyleName(RESOURCES.getCSS().flaggingPanel());
		
		panels = new ArrayList<>();
		initPanel(eventBus);
		
		this.setVisible(false);
		
        eventBus.addHandler(DiagramObjectsFlagResetEvent.TYPE, this);
        eventBus.addHandler(DiagramObjectsFlagRequestedEvent.TYPE, this);
        eventBus.addHandler(DiagramObjectsFlaggedEvent.TYPE, this);
	}
	
	@Override
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return addDomHandler(handler, MouseOverEvent.getType());
	}
	
	@Override
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return addDomHandler(handler, MouseOutEvent.getType());
	}
	
	protected void initPanel(EventBus eventBus) {
		
		FlaggedInteractorSetLegend legend = new FlaggedInteractorSetLegend(eventBus);
		IDGFlaggedItemsControl control = new IDGFlaggedItemsControl(eventBus);
		
		panels.add(legend);
		panels.add(control);
		
		this.add(legend);
		this.add(control);
		
		this.addMouseOverHandler(e -> {
			legend.setVisible(true);
			control.setVisible(true);
		});
		this.addMouseOutHandler(e -> {
			legend.setVisible(false);
			control.setVisible(false);
		});
	}
	
	@Override
	public void onDiagramObjectsFlagRequested(DiagramObjectsFlagRequestedEvent event) {
		this.setVisible(true);
	}
	
	@Override
	public void onDiagramObjectsFlagged(DiagramObjectsFlaggedEvent event) {
		Timer timer = new Timer() {
			@Override
			public void run() {
				for(AbsolutePanel panel : panels) {
					panel.setVisible(false);
				}
			}
		};
		timer.schedule(3000);
	}

	@Override
	public void onDiagramObjectsFlagReset(DiagramObjectsFlagResetEvent event) {
		this.setVisible(false);
	}
	
	/*
	 * Everything below here is for styling
	 * */
	public static Resources RESOURCES;
	static {
		RESOURCES = GWT.create(Resources.class);
		RESOURCES.getCSS().ensureInjected();
	}
	
	public interface Resources extends ClientBundle {
		@Source(ResourceCSS.CSS)
		ResourceCSS getCSS();
	}
	
	@CssResource.ImportedWithPrefix("idg-diagram-idgFlaggingPanel")
	public interface ResourceCSS extends CssResource {
		String CSS = "org/reactome/web/fi/legends/IDGFlaggingPanel.css";
		
		String flaggingPanel();
	}
}
