package org.reactome.web.idg.client;

import org.reactome.web.diagram.client.ViewerContainer;
import org.reactome.web.diagram.common.IconButton;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.content.Content;
import org.reactome.web.idg.client.visualisers.fiview.FIViewVisualiser;
import org.reactome.web.idg.client.flag.CytoscapeViewFlag;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;


public class IdgViewerContainer extends ViewerContainer implements ClickHandler{

	private IconButton fiviewButton;
	private FIViewVisualiser fIViewVisualiser;
	
	public IdgViewerContainer(EventBus eventBus) {
		super(eventBus);
		
	}

	@Override
	protected void initialise() {
		super.initialise();
		
		fiviewButton = new IconButton(IDGRESOURCES.cytoscapeIcon(), IDGRESOURCES.getCSS().cytoscape(), "Cytoscape View", this);
		super.leftTopLauncher.getMainControlPanel().add(fiviewButton);
		
		fIViewVisualiser = new FIViewVisualiser(eventBus);
		super.add(fIViewVisualiser);
		
	}
	
	@Override
	public void onClick(ClickEvent event) {
		CytoscapeViewFlag.toggleCytoscapeViewFlag();
		setActiveVisualiser(getContext());
	}
	
	@Override
	protected void setActiveVisualiser(Context context) {
		super.setActiveVisualiser(context);
		if(getContext().getContent().getType() == Content.Type.SVG && CytoscapeViewFlag.isCytoscapeViewFlag()) {
			visualisers.get(getContext().getContent().getType()).asWidget().setVisible(false);
			fIViewVisualiser.asWidget().setVisible(true);
			activeVisualiser = fIViewVisualiser;
		}
	}
	
	
	/**
	 * Everything below here is for resource loading for the cytoscape view button.
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

    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-MainControlPanel")
    public interface IDGResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/idg/client/CytoscapeButton.css";

        String cytoscape();
    }
}
