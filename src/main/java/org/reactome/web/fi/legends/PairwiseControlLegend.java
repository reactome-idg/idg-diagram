package org.reactome.web.fi.legends;

import java.util.ArrayList;
import java.util.List;

import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.legends.LegendPanel;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayObject;
import org.reactome.web.fi.events.PairwiseInteractorsResetEvent;
import org.reactome.web.fi.events.PairwiseNumbersLoadedEvent;
import org.reactome.web.fi.handlers.PairwiseNumbersLoadedHandler;
import org.reactome.web.fi.tools.popup.IDGPopupFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;

public class PairwiseControlLegend extends LegendPanel implements PairwiseNumbersLoadedHandler{

	private FlowPanel innerPanel;
	private PwpButton closeBtn;
	
	public PairwiseControlLegend(EventBus eventBus) {
		super(eventBus);
		LegendPanelCSS css = RESOURCES.getCSS();
		
		eventBus.addHandler(PairwiseNumbersLoadedEvent.TYPE, this);
		
		initPanel(css);
	}

	private void initPanel(LegendPanelCSS css) {
		this.getElement().getStyle().setMarginBottom(10, Unit.PX);
		
		this.innerPanel = new FlowPanel();
		innerPanel.setStyleName(IDGRESOURCES.getCSS().innerPanel());
		this.add(innerPanel);
		
		this.closeBtn = new PwpButton("Close",  css.close(), e->closeButtonHandler());
		this.add(this.closeBtn);
		
		addStyleName(css.expressionControl());
		this.setVisible(false);
	}
	
	@Override
	public void onPairwiseNumbersLoaded(PairwiseNumbersLoadedEvent event) {
		innerPanel.clear();
		
		List<String> ids = new ArrayList<>();
		IDGPopupFactory.get().getCurrentPairwiseProperties().forEach(prop -> {
			ids.add(prop.getId());
		});
		
		InlineLabel label = new InlineLabel(String.join(", ", ids));
		label.setStyleName(IDGRESOURCES.getCSS().textOverflow());
		innerPanel.add(label);
		innerPanel.setTitle(String.join("\n",ids));
		this.setVisible(true);
	}
	
	private void closeButtonHandler() {
		IDGPopupFactory.get().setCurrentPairwiseProperties(new ArrayList<PairwiseOverlayObject>());
		eventBus.fireEventFromSource(new PairwiseInteractorsResetEvent(), this);
		this.setVisible(false);
	}

	public static Resources IDGRESOURCES;
	static {
		IDGRESOURCES = GWT.create(Resources.class);
		IDGRESOURCES.getCSS().ensureInjected();
	}
	
	public interface Resources extends ClientBundle{
		@Source(ResourceCSS.CSS)
		ResourceCSS getCSS();
	}
	
	@CssResource.ImportedWithPrefix("idgDiagram-PairwiseControlLegend")
	public interface ResourceCSS extends CssResource {
		String CSS = "org/reactome/web/fi/legends/PairwiseControlLegend.css";
		
		String innerPanel();
		
		String textOverflow();
	}
}
