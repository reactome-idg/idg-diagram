package org.reactome.web.idg.client;

import org.reactome.web.diagram.client.ViewerContainer;
import org.reactome.web.diagram.common.IconButton;
import org.reactome.web.idg.client.visualisers.fiview.FIViewVisualiser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public class IdgViewerContainer extends ViewerContainer implements ClickHandler{

	EventBus eventBus;
	
	IconButton fiviewButton;
	
	public IdgViewerContainer(EventBus eventBus) {
		super(eventBus);
		this.eventBus = eventBus;
		
		initialise();
	}

	@Override
	protected void initialise() {
		fiviewButton = new IconButton(RESOURCES.cytoscapeIcon(), RESOURCES.getCSS().cytoscape(), "Cytoscape View", this);
		super.leftTopLauncher.getMainControlPanel().add(fiviewButton);
		super.initialise();
		this.add(new FIViewVisualiser(eventBus));
		
	}
	
	@Override
	public void onClick(ClickEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Everything below has to do with loading static resources for image and css generation
	 */
    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }
    /**
     * A ClientBundle of resources used by this widget.
     */
    public interface Resources extends ClientBundle {
        /**
         * The styles used in this widget.
         */
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

        @Source("images/Cytoscape.png")
        ImageResource cytoscapeIcon();
    }
    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-MainControlPanel")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/idg/client/cyoscape-button.css";

        String cytoscape();
    }
}
