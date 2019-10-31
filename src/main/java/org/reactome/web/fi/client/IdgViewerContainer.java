package org.reactome.web.fi.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.reactome.web.diagram.client.ViewerContainer;
import org.reactome.web.diagram.client.visualisers.Visualiser;
import org.reactome.web.diagram.client.visualisers.diagram.DiagramVisualiser;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.content.Content;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.events.RenderOtherDataEvent;
import org.reactome.web.diagram.handlers.RenderOtherDataHandler;
import org.reactome.web.fi.client.visualisers.OverlayDataHandler;
import org.reactome.web.fi.client.visualisers.diagram.profiles.OverlayColours;
import org.reactome.web.fi.client.visualisers.diagram.renderers.ProteinTargetLevelRenderer;
import org.reactome.web.fi.client.visualisers.fiview.FIViewVisualiser;
import org.reactome.web.fi.common.CytoscapeViewFlag;
import org.reactome.web.fi.common.IDGIconButton;
import org.reactome.web.fi.data.overlay.RawOverlayEntities;
import org.reactome.web.fi.events.CytoscapeToggledEvent;
import org.reactome.web.fi.events.OverlayDataLoadedEvent;
import org.reactome.web.fi.events.OverlayDataRequestedEvent;
import org.reactome.web.fi.events.OverlayDataResetEvent;
import org.reactome.web.fi.events.MakeOverlayRequestEvent;
import org.reactome.web.fi.handlers.OverlayDataLoadedHandler;
import org.reactome.web.fi.handlers.OverlayDataResetHandler;
import org.reactome.web.fi.handlers.MakeOverlayRequestHandler;
import org.reactome.web.fi.legends.OverlayLegend;
import org.reactome.web.fi.model.OverlayType;
import org.reactome.web.fi.overlay.OverlayDialogPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * 
 * @author brunsont
 *
 */
public class IdgViewerContainer extends ViewerContainer implements RenderOtherDataHandler,
OverlayDataLoadedHandler, OverlayDataResetHandler, MakeOverlayRequestHandler{

	private IDGIconButton fiviewButton;
	private IDGIconButton diagramButton;
	private FIViewVisualiser fIViewVisualiser;
	private IDGIconButton overlayButton;
	private OverlayLegend overlayLegend;
	private OverlayDialogPanel overlayDialogPanel;
	
	private RawOverlayEntities overlayEntities;
	
	public IdgViewerContainer(EventBus eventBus) {
		super(eventBus);
		
		initHandlers();
	}

	private void initHandlers() {
		eventBus.addHandler(RenderOtherDataEvent.TYPE, this);
		eventBus.addHandler(OverlayDataLoadedEvent.TYPE, this);
		eventBus.addHandler(OverlayDataResetEvent.TYPE, this);
		eventBus.addHandler(MakeOverlayRequestEvent.TYPE, this);
	}

	@Override
	protected void initialise() {
		super.initialise();
		
		fiviewButton = new IDGIconButton(IDGRESOURCES.cytoscapeIcon(), IDGRESOURCES.getCSS().cytoscape(), "Cytoscape View");
		diagramButton = new IDGIconButton(IDGRESOURCES.diagramIcon(), IDGRESOURCES.getCSS().diagram(), "Diagram View");
		overlayButton = new IDGIconButton(IDGRESOURCES.overlayIcon(), IDGRESOURCES.getCSS().cytoscape(), "Select An Overlay");
		overlayLegend = new OverlayLegend(eventBus);
		overlayDialogPanel = new OverlayDialogPanel(eventBus);
		overlayDialogPanel.hide();
				
		//adds diagramButton and fiviewButton. sets fiview button as default to show
		super.leftTopLauncher.getMainControlPanel().add(diagramButton);
		super.leftTopLauncher.getMainControlPanel().getWidget(
				super.leftTopLauncher.getMainControlPanel()
				.getWidgetIndex(diagramButton)).setVisible(false);
		super.leftTopLauncher.getMainControlPanel().add(fiviewButton);
		super.leftTopLauncher.getMainControlPanel().add(overlayButton);
		
		super.bottomContainerPanel.add(overlayLegend);
		
		this.add(overlayDialogPanel);
		int x = activeVisualiser.asWidget().getAbsoluteLeft();
		int y = activeVisualiser.asWidget().getAbsoluteTop();
		overlayDialogPanel.setPopupPosition(x+20, y+80);
		
		bind();
		
		OverlayDataHandler.getHandler().registerHelper(new ProteinTargetLevelRenderer(eventBus));

	}
	
	@Override
	protected void addExternalVisualisers() {
		fIViewVisualiser = new FIViewVisualiser(eventBus);
		super.add(fIViewVisualiser);//TODO: move this so that buttons are accessible over it
	}

	@Override
	protected void setActiveVisualiser(Context context) {
		hideButtons();
		if(context.getContent().getType() == Content.Type.DIAGRAM && CytoscapeViewFlag.isCytoscapeViewFlag()) {
			for (Visualiser vis : visualisers.values()) {
				vis.asWidget().setVisible(false);
			}
			fIViewVisualiser.asWidget().setVisible(true);
			showDiagramButton();
			activeVisualiser = fIViewVisualiser;
			return;
		}
		else if(context.getContent().getType() == Content.Type.DIAGRAM && !CytoscapeViewFlag.isCytoscapeViewFlag()) {
			showCytoscapeButton();
			super.setActiveVisualiser(context);
		}
		super.setActiveVisualiser(context);
		
		//perform node overlay if overlayEntities exist
		if(activeVisualiser instanceof FIViewVisualiser && overlayEntities !=null)
			((FIViewVisualiser)activeVisualiser).overlayNodes(overlayEntities);
	}
	
	@Override
	public void contentLoaded(Context context) {
		super.contentLoaded(context);
		
		//check if overlay should be loaded and if so, load new TCRD data
		if(overlayEntities != null) {
			String overlayType = overlayEntities.getDataType();
			eventBus.fireEventFromSource(new OverlayDataResetEvent(), this);
			eventBus.fireEventFromSource(new MakeOverlayRequestEvent(OverlayType.getType(overlayType)), this);
		}
	}

	private void bind() {
		fiviewButton.addClickHandler(e -> cytoscapeButtonPressed());
		diagramButton.addClickHandler(e -> cytoscapeButtonPressed());
		overlayButton.addClickHandler(e -> overlayDialogPanel.show()); 
	}
	
	@Override
	public void onMakeOverlayRequest(MakeOverlayRequestEvent event) {
		Set<String> identifiers = null;
		
		if(overlayEntities != null)
			if(OverlayType.getType(overlayEntities.getDataType()) == event.getType())
				return;	

		if(activeVisualiser instanceof FIViewVisualiser)
			identifiers = context.getContent().getIdentifierMap().keySet();
		
		//in case of DiagramVisualiser, get each physical entity identifier and add to set
		else if(activeVisualiser instanceof DiagramVisualiser) {
			
			//iterate over all diagram objects in a diagram
			for(DiagramObject  diagramObject: context.getContent().getDiagramObjects()) {
				Set<GraphPhysicalEntity> participants = null;
				
				//Get graph object of each diagramObject, check if its a GraphPhysicalEntity,
				//and get each participant if so. Then add identifier of each participant
				//to a set of identifiers.
				GraphObject graphObject = diagramObject.getGraphObject();
				if(graphObject instanceof GraphPhysicalEntity) {
					GraphPhysicalEntity pe = (GraphPhysicalEntity) graphObject;
					participants = pe.getParticipants();
					if(identifiers==null)
						identifiers = new HashSet<>();
					for(GraphPhysicalEntity participant: participants) {
						identifiers.add(participant.getIdentifier()); 
					}
				}
			}
		}
		eventBus.fireEventFromSource(new OverlayDataRequestedEvent(identifiers, event.getType()), this);
	}
	
	private void cytoscapeButtonPressed() {
		CytoscapeViewFlag.toggleCytoscapeViewFlag();
		eventBus.fireEventFromSource(new CytoscapeToggledEvent(getContext()), this);

	}
	
	private void showCytoscapeButton() {
		super.leftTopLauncher.getMainControlPanel().getWidget(
				super.leftTopLauncher.getMainControlPanel()
				.getWidgetIndex(fiviewButton)).setVisible(true);
	}
	private void showDiagramButton() {
		super.leftTopLauncher.getMainControlPanel().getWidget(
				super.leftTopLauncher.getMainControlPanel()
				.getWidgetIndex(diagramButton)).setVisible(true);
	}
	
	private void hideButtons() {
		super.leftTopLauncher.getMainControlPanel().getWidget(
				super.leftTopLauncher.getMainControlPanel()
				.getWidgetIndex(fiviewButton)).setVisible(false);
		super.leftTopLauncher.getMainControlPanel().getWidget(
				super.leftTopLauncher.getMainControlPanel()
				.getWidgetIndex(diagramButton)).setVisible(false);
	}
	
	@Override
	public void onRenderOtherData(RenderOtherDataEvent event) {
		if(this.overlayEntities == null)
			return;
		
		OverlayDataHandler.getHandler()
						  .overlayData(event.getItems(), 
									   event.getCtx(), 
									   context, 
									   event.getRendererManager(), 
									   this.overlayEntities,
									   event.getOverlay());
	}
	
	@Override
	public void onOverlayDataLoaded(OverlayDataLoadedEvent event) {
		this.overlayEntities = event.getEntities();
		context.setDialogMap(new HashMap<>());
		if(activeVisualiser instanceof DiagramVisualiser) 
			activeVisualiser.loadAnalysis();
		else if(activeVisualiser instanceof FIViewVisualiser)
			((FIViewVisualiser)activeVisualiser).overlayNodes(overlayEntities);
	}
	
	@Override
	public void onOverlayDataReset(OverlayDataResetEvent event) {
		overlayEntities = null;
		context.setDialogMap(new HashMap<>());
		overlayDialogPanel.hide();
		if(activeVisualiser instanceof DiagramVisualiser)
			activeVisualiser.loadAnalysis();
		else if(activeVisualiser instanceof FIViewVisualiser)
			((FIViewVisualiser)activeVisualiser).overlayNodes(overlayEntities);
	}
	
	@Override
	public void onResize() {
		super.onResize();
		fIViewVisualiser.setSize(this.getOffsetWidth(), this.getOffsetHeight());
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
        
        @Source("images/EHLDIcon.png")
        ImageResource diagramIcon();
        
        @Source("images/OverlayIcon.png")
        ImageResource overlayIcon();
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-MainControlPanel")
    public interface IDGResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/fi/client/IconButton.css";

        String cytoscape();
        
        String diagram();
    }
}
