package org.reactome.web.fi.legends;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.reactome.web.diagram.events.DiagramObjectsFlagResetEvent;
import org.reactome.web.diagram.events.DiagramObjectsFlaggedEvent;
import org.reactome.web.diagram.handlers.DiagramObjectsFlagResetHandler;
import org.reactome.web.diagram.handlers.DiagramObjectsFlaggedHandler;
import org.reactome.web.gwtCytoscapeJs.util.Console;

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
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class FlaggedInteractorSetLegend extends AbsolutePanel implements DiagramObjectsFlaggedHandler,
DiagramObjectsFlagResetHandler, HasMouseOverHandlers, HasMouseOutHandlers{

	private EventBus eventBus;
	
	protected FlowPanel dataDescContainer;
	
	public FlaggedInteractorSetLegend(EventBus eventBus) {
		this.eventBus = eventBus;
		
		this.setStyleName(RESOURCES.getCSS().panel());
		this.addStyleName(RESOURCES.getCSS().increaseTransparency());
		
		this.dataDescContainer = new FlowPanel();
		this.add(dataDescContainer);
		
		initHandlers();
	}
	
	@Override
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return addDomHandler(handler, MouseOverEvent.getType());
	}
	
	@Override
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return addDomHandler(handler, MouseOutEvent.getType());
	}
	
	private void initHandlers() {
		eventBus.addHandler(DiagramObjectsFlaggedEvent.TYPE, this);
		eventBus.addHandler(DiagramObjectsFlagResetEvent.TYPE, this);
		this.addMouseOverHandler(e -> {
			this.removeStyleName(RESOURCES.getCSS().increaseTransparency());
		});
		this.addMouseOutHandler(e -> {
			this.addStyleName(RESOURCES.getCSS().increaseTransparency());
		});
	}
	
	@Override
	public void onDiagramObjectsFlagged(DiagramObjectsFlaggedEvent event) {
		String flagTerm = event.getTerm();
		if(!event.getIncludeInteractors()) return;
		
		Set<String> descs = new HashSet<>();
		if(flagTerm.contains(",")) {
			descs = new HashSet<>(Arrays.asList(flagTerm.split(",")));
			descs.remove(flagTerm.substring(0, flagTerm.indexOf(",")));
		}
		if(descs.size() == 0) return;
		
		
		dataDescContainer.clear();
		descs.forEach(e -> {
			Label lbl = new Label(e);
			lbl.setStyleName(RESOURCES.getCSS().descLabel());
			dataDescContainer.add(new Label(e));
		});
		this.setVisible(true);
	}
	
	@Override
	public void onDiagramObjectsFlagReset(DiagramObjectsFlagResetEvent event) {
		this.dataDescContainer.clear();
		this.setVisible(false);
	}

	public static Resources RESOURCES;
	static {
		RESOURCES = GWT.create(Resources.class);
		RESOURCES.getCSS().ensureInjected();
	}
	
	public interface Resources extends ClientBundle {
		@Source (ResourceCSS.CSS)
		ResourceCSS getCSS();
	}
	
	@CssResource.ImportedWithPrefix("idgDiagram-FlagedInteractorLegend")
	public interface ResourceCSS extends CssResource {
		String CSS = "org/reactome/web/fi/legends/FlaggedInteractorSetLegend.css";
	
		String panel();
		
		String descLabel();
		
		String increaseTransparency();
	}
}
