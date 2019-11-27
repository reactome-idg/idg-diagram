package org.reactome.web.fi.legends;

import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.fi.model.DataOverlay;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * 
 * @author brunsont
 *
 */
public class ContinuousControlPanel extends FlowPanel implements ClickHandler{

	private EventBus eventBus;
	private PwpButton backButton;
	private PwpButton forwardButton;
	
	public ContinuousControlPanel(EventBus eventBus, DataOverlay dataOverlay) {
		this.eventBus = eventBus;
		
		backButton = new PwpButton("Show previous", RESOURCES.getCSS().back(), this);
		this.add(backButton);
		forwardButton = new PwpButton("Show next", RESOURCES.getCSS().forward(), this);
		this.add(forwardButton);
		
		FlowPanel infoPanel = new FlowPanel();
		InlineLabel stepLabel = new InlineLabel((dataOverlay.getColumn()+1) + "/" + dataOverlay.getTissueTypes().size() + "  ");
		infoPanel.add(stepLabel);
		InlineLabel typeTissueLabel  = new InlineLabel(dataOverlay.getEType() + " - " + dataOverlay.getTissueTypes().get(dataOverlay.getColumn()));
		infoPanel.add(typeTissueLabel);
		this.add(infoPanel);
	}
	
	@Override
	public void onClick(ClickEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	//Below here is for styling
	public static Resources RESOURCES;
	static {
		RESOURCES = GWT.create(Resources.class);
		RESOURCES.getCSS().ensureInjected();
	}
	public interface Resources extends ClientBundle{
		@Source(ResourceCSS.CSS)
		ResourceCSS getCSS();
		
		@Source("images/forward_clicked.png")
		ImageResource forwardClicked();
		
		@Source("images/forward_disabled.png")
		ImageResource forwardDisabled();
		
		@Source("images/forward_hovered.png")
		ImageResource forwardHovered();
		
		@Source("images/forward_normal.png")
		ImageResource forwardNormal();
		
		@Source("images/rewind_clicked.png")
		ImageResource backClicked();
		
		@Source("images/rewind_disabled.png")
		ImageResource backDisabled();
		
		@Source("images/rewind_hovered.png")
		ImageResource backHovered();
		
		@Source("images/rewind_normal.png")
		ImageResource backNormal();
	}
	@CssResource.ImportedWithPrefix("idgDiagram-ContinuousControlPanel")
	public interface ResourceCSS extends CssResource{
		String CSS = "org/reactome/web/fi/legends/ContinuousControlPanel.css";
		
		String forward();
		
		String back();
	}
}
