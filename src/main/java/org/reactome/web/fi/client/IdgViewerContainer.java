package org.reactome.web.fi.client;

import org.reactome.web.diagram.client.ViewerContainer;
import org.reactome.web.diagram.client.visualisers.Visualiser;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.content.Content;
import org.reactome.web.fi.client.visualisers.fiview.FIViewVisualiser;
import org.reactome.web.fi.common.CytoscapeViewFlag;
import org.reactome.web.fi.common.IDGIconButton;
import org.reactome.web.fi.events.CytoscapeToggledEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;

/**
 * 
 * @author brunsont
 *
 */
public class IdgViewerContainer extends ViewerContainer {

	private IDGIconButton fiviewButton;
	private IDGIconButton diagramButton;
	private FIViewVisualiser fIViewVisualiser;
	
	public IdgViewerContainer(EventBus eventBus) {
		super(eventBus);
		
	}

	@Override
	protected void initialise() {
		super.initialise();
		
		fiviewButton = new IDGIconButton(IDGRESOURCES.cytoscapeIcon(), IDGRESOURCES.getCSS().cytoscape(), "Cytoscape View");
		diagramButton = new IDGIconButton(IDGRESOURCES.diagramIcon(), IDGRESOURCES.getCSS().diagram(), "Diagram View");
		
		//CytoscapeViewFlag is false by default so set button to fiviewButton by default
		super.leftTopLauncher.getMainControlPanel().add(fiviewButton);
		
		
		bind();
	}
	
	
	
	@Override
	protected void addExternalVisualisers() {
		fIViewVisualiser = new FIViewVisualiser(eventBus);
		super.add(fIViewVisualiser);//TODO: move this so that buttons are accessible over it
	}

	@Override
	protected void setActiveVisualiser(Context context) {
		if(context.getContent().getType() == Content.Type.DIAGRAM && CytoscapeViewFlag.isCytoscapeViewFlag()) {
			for (Visualiser vis : visualisers.values()) {
				vis.asWidget().setVisible(false);
			}
			fIViewVisualiser.asWidget().setVisible(true);
			activeVisualiser = fIViewVisualiser;
			return;
		}
		super.setActiveVisualiser(context);
	}
	
	private void bind() {
		fiviewButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				cytoscapeButtonPressed();
				
			}			
		});
		diagramButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				cytoscapeButtonPressed();
			}
		});
	}
	
	private void cytoscapeButtonPressed() {
		CytoscapeViewFlag.toggleCytoscapeViewFlag();
		switchCytoscapeToggleButtons();
		eventBus.fireEventFromSource(new CytoscapeToggledEvent(getContext()), this);
	}
	
	private void switchCytoscapeToggleButtons() {
		if(CytoscapeViewFlag.isCytoscapeViewFlag()) {
			super.leftTopLauncher.getMainControlPanel().remove(
					super.leftTopLauncher.getMainControlPanel().getWidgetIndex(fiviewButton));
			super.leftTopLauncher.getMainControlPanel().add(diagramButton);
			return;
		}
		super.leftTopLauncher.getMainControlPanel().remove(
				super.leftTopLauncher.getMainControlPanel().getWidgetIndex(diagramButton));
		super.leftTopLauncher.getMainControlPanel().add(fiviewButton);

	}
	
	/**
	 * Everything below here is for resources.
	 */
    public static IDGResources IDGRESOURCES;
    static {
        IDGRESOURCES = GWT.create(IDGResources.class);
        IDGRESOURCES.getCSS().ensureInjected();
    }

    /**
     * A ClientBundle of resources used by this widget.
     */
    public interface IDGResources extends ClientBundle {
        /**
         * The styles used in this widget.
         */
        @Source(IDGResourceCSS.CSS)
        IDGResourceCSS getCSS();

        @Source("images/Cytoscape.png")
        ImageResource cytoscapeIcon();
        
        @Source("images/EHLDPathway.png")
        ImageResource diagramIcon();
        
        

    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-MainControlPanel")
    public interface IDGResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/fi/client/CytoscapeButton.css";

        String cytoscape();
        
        String diagram();
    }
}