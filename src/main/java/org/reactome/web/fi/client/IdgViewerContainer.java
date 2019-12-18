package org.reactome.web.fi.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.reactome.web.diagram.client.ViewerContainer;
import org.reactome.web.diagram.client.visualisers.Visualiser;
import org.reactome.web.diagram.client.visualisers.diagram.DiagramVisualiser;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.content.Content;
import org.reactome.web.diagram.data.graph.model.GraphEntityWithAccessionedSequence;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.graph.model.GraphProteinDrug;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.events.AnalysisResetEvent;
import org.reactome.web.diagram.events.RenderOtherDataEvent;
import org.reactome.web.diagram.handlers.RenderOtherDataHandler;
import org.reactome.web.diagram.util.MapSet;
import org.reactome.web.fi.client.visualisers.OverlayDataHandler;
import org.reactome.web.fi.client.visualisers.diagram.renderers.ContinuousDataOverlayRenderer;
import org.reactome.web.fi.client.visualisers.diagram.renderers.DiscreteDataOverlayRenderer;
import org.reactome.web.fi.client.visualisers.fiview.FIViewVisualiser;
import org.reactome.web.fi.common.CytoscapeViewFlag;
import org.reactome.web.fi.common.IDGIconButton;
import org.reactome.web.fi.data.overlay.model.OverlayProperties;
import org.reactome.web.fi.events.CytoscapeToggledEvent;
import org.reactome.web.fi.events.DataOverlayColumnChangedEvent;
import org.reactome.web.fi.events.OverlayDataLoadedEvent;
import org.reactome.web.fi.events.OverlayDataRequestedEvent;
import org.reactome.web.fi.events.OverlayDataResetEvent;
import org.reactome.web.fi.events.MakeOverlayRequestEvent;
import org.reactome.web.fi.handlers.OverlayDataLoadedHandler;
import org.reactome.web.fi.handlers.OverlayDataResetHandler;
import org.reactome.web.fi.handlers.DataOverlayColumnChangedHandler;
import org.reactome.web.fi.handlers.MakeOverlayRequestHandler;
import org.reactome.web.fi.legends.OverlayColourLegend;
import org.reactome.web.fi.legends.OverlayControlLegend;
import org.reactome.web.fi.model.DataOverlay;
import org.reactome.web.fi.model.DataOverlayEntity;
import org.reactome.web.fi.model.OverlayDataType;
import org.reactome.web.fi.tools.overlay.OverlayLauncherDisplay;

import com.google.gwt.core.client.GWT;
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
public class IdgViewerContainer extends ViewerContainer implements RenderOtherDataHandler,
OverlayDataLoadedHandler, OverlayDataResetHandler, MakeOverlayRequestHandler, DataOverlayColumnChangedHandler{

	private IDGIconButton fiviewButton;
	private IDGIconButton diagramButton;
	private FIViewVisualiser fIViewVisualiser;
	private IDGIconButton overlayButton;
	private OverlayColourLegend overlayColourLegend;
	private OverlayControlLegend overlayControlLegend;
	private OverlayLauncherDisplay overlayLauncher;
	
	private DataOverlay dataOverlay;
	private boolean renderOverlays = false;
	
	private OverlayProperties lastOverlayProperties = null;
	
	public IdgViewerContainer(EventBus eventBus) {
		super(eventBus);
		
		initHandlers();
	}

	private void initHandlers() {
		eventBus.addHandler(RenderOtherDataEvent.TYPE, this);
		eventBus.addHandler(OverlayDataLoadedEvent.TYPE, this);
		eventBus.addHandler(OverlayDataResetEvent.TYPE, this);
		eventBus.addHandler(MakeOverlayRequestEvent.TYPE, this);
		eventBus.addHandler(DataOverlayColumnChangedEvent.TYPE, this);
	}

	@Override
	protected void initialise() {		
		overlayColourLegend = new OverlayColourLegend(eventBus);
		super.rightContainerPanel.add(overlayColourLegend);
		super.initialise();
		
		fiviewButton = new IDGIconButton(IDGRESOURCES.cytoscapeIcon(), IDGRESOURCES.getCSS().cytoscape(), "Cytoscape View");
		diagramButton = new IDGIconButton(IDGRESOURCES.diagramIcon(), IDGRESOURCES.getCSS().diagram(), "Diagram View");
		overlayButton = new IDGIconButton(IDGRESOURCES.overlayIcon(), IDGRESOURCES.getCSS().cytoscape(), "Select An Overlay");
		overlayLauncher = new OverlayLauncherDisplay(eventBus);
				
		//adds diagramButton and fiviewButton. sets fiview button as default to show
		super.leftTopLauncher.getMainControlPanel().add(diagramButton);
		super.leftTopLauncher.getMainControlPanel().getWidget(
				super.leftTopLauncher.getMainControlPanel()
				.getWidgetIndex(diagramButton)).setVisible(false);
		super.leftTopLauncher.getMainControlPanel().add(fiviewButton);
		super.leftTopLauncher.getMainControlPanel().add(overlayButton);
		overlayControlLegend = new OverlayControlLegend(eventBus);
		super.bottomContainerPanel.add(overlayControlLegend);
		this.add(overlayLauncher);
		
		
		
		bind();
		
		OverlayDataHandler.getHandler().registerHelper(new DiscreteDataOverlayRenderer(eventBus));
		OverlayDataHandler.getHandler().registerHelper(new ContinuousDataOverlayRenderer(eventBus));
		
	}
	
	@Override
	protected void addExternalVisualisers() {
		fIViewVisualiser = new FIViewVisualiser(eventBus);
		super.add(fIViewVisualiser);
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
	}
	
	@Override
	public void contentLoaded(Context context) {
		super.contentLoaded(context);
		
		//check if overlay should be loaded and if so, load new TCRD data
		if(dataOverlay != null) {
			OverlayDataType overlayType = dataOverlay.getOverlayType();
			eventBus.fireEventFromSource(new OverlayDataResetEvent(), this);
			
			//don't bother reloading when new content is an SVG
			if(context.getContent().getType() != Content.Type.SVG)
				eventBus.fireEventFromSource(new MakeOverlayRequestEvent(overlayType, this.lastOverlayProperties), this);
		}
	}

	private void bind() {
		fiviewButton.addClickHandler(e -> cytoscapeButtonPressed());
		diagramButton.addClickHandler(e -> cytoscapeButtonPressed());
		overlayButton.addClickHandler(e -> toggleOverlayPanel()); 
	}
	
	private void toggleOverlayPanel() {
		overlayLauncher.center();
		overlayLauncher.show();
	}

	@Override
	public void onMakeOverlayRequest(MakeOverlayRequestEvent event) {
		
		//set lastExpressionOverlayValueType in case diagram is changed before reset
		this.lastOverlayProperties = event.getOverlayProperties();
		//can't have an overlay and Analysis at the same time
		if(context.getAnalysisStatus() != null)
			eventBus.fireEventFromSource(new AnalysisResetEvent(), this);
		
		if(event.getEntityType() != null) 
			requestOverlayDataEntities(event);
		else if(event.getDataType() != null)
			requestOverlayData(event);
		else
			GWT.log("Cant figure out what overlay type to request!");
	}

	private void requestOverlayDataEntities(MakeOverlayRequestEvent event) {
		GWT.log("Request overlay data entities!");
	}

	private void requestOverlayData(MakeOverlayRequestEvent event) {
		Set<String> identifiers = null;

		eventBus.fireEventFromSource(new OverlayDataResetEvent(), this);
		
		if(activeVisualiser instanceof FIViewVisualiser)
			identifiers = context.getContent().getIdentifierMap().keySet();
		
		//in case of DiagramVisualiser, get each physical entity identifier and add to set
		else if(activeVisualiser instanceof DiagramVisualiser) {
			identifiers = collectDiagramInteractors(identifiers);
		}
		
		event.getOverlayProperties().setUniprots(getPostData(identifiers));
		
		if(event.getDataType() == OverlayDataType.TISSUE_EXPRESSION) {
	        eventBus.fireEventFromSource(new OverlayDataRequestedEvent(event.getDataType(), event.getOverlayProperties()), this);
		}
	}

	private Set<String> collectDiagramInteractors(Set<String> identifiers) {
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
					if(participant instanceof GraphEntityWithAccessionedSequence || participant instanceof GraphProteinDrug) {
						int i= participant.getIdentifier().length();
						if(participant.getIdentifier().contains("-"))
							i = participant.getIdentifier().indexOf("-");
						identifiers.add(participant.getIdentifier().substring(0, i)); 
					}
				}
			}
		}
		return identifiers;
	}
	
	/**
	 * iterates over a set of uniprot identifiers and adds them to a string delineated by ','.
	 * @param ids
	 * @return
	 */
	private String getPostData(Set<String> ids) {
		StringBuilder post = new StringBuilder();
		ids.stream().forEach(S -> post.append(S).append(","));
		if(post.length()>0) {
			post.delete(post.length()-1, post.length());
			return post.toString();
		}
		
		return null;
	}
	
	private void cytoscapeButtonPressed() {
		CytoscapeViewFlag.toggleCytoscapeViewFlag();
		eventBus.fireEventFromSource(new CytoscapeToggledEvent(getContext()), this);

	}
	
	private void showCytoscapeButton() {
		super.leftTopLauncher.getMainControlPanel().getWidget(
				super.leftTopLauncher.getMainControlPanel()
				.getWidgetIndex(fiviewButton)).setVisible(true);
		showOverlayButton();
	}

	private void showDiagramButton() {
		super.leftTopLauncher.getMainControlPanel().getWidget(
				super.leftTopLauncher.getMainControlPanel()
				.getWidgetIndex(diagramButton)).setVisible(true);
		showOverlayButton();
	}
	
	private void showOverlayButton() {
		super.leftTopLauncher.getMainControlPanel().getWidget(
				super.leftTopLauncher.getMainControlPanel()
				.getWidgetIndex(overlayButton)).setVisible(true);
	}
	
	/**
	 * hides fiview, diagram, and overlay button. Also hides overlayDialogPanel
	 */
	private void hideButtons() {
		super.leftTopLauncher.getMainControlPanel().getWidget(
				super.leftTopLauncher.getMainControlPanel()
				.getWidgetIndex(fiviewButton)).setVisible(false);
		super.leftTopLauncher.getMainControlPanel().getWidget(
				super.leftTopLauncher.getMainControlPanel()
				.getWidgetIndex(diagramButton)).setVisible(false);
		super.leftTopLauncher.getMainControlPanel().getWidget(
				super.leftTopLauncher.getMainControlPanel()
				.getWidgetIndex(overlayButton)).setVisible(false);
		overlayLauncher.hide();
	}
	
	/**
	 * Directs rendering of data on diagrams after the base diagram is rendered
	 */
	@Override
	public void onRenderOtherData(RenderOtherDataEvent event) {
		if(this.renderOverlays == false)
			return;
		
		OverlayDataHandler.getHandler()
						  .overlayData(event.getItems(), 
									   event.getCtx(), 
									   context, 
									   event.getRendererManager(), 
									   this.dataOverlay,
									   event.getOverlay());
	}
	
	@Override
	public void onOverlayDataLoaded(OverlayDataLoadedEvent event) {
		this.overlayLauncher.hide();
		this.renderOverlays = true;
		this.dataOverlay = event.getDataOverlay();
		this.overlayColourLegend.setUnit(dataOverlay.getOverlayProperties().getUnit());
		context.setDialogMap(new HashMap<>());
		
		//testing new way to set is hit for all data so it works in FIViz without overlaying on diagram first
		setIsHitValues();
		
		if(activeVisualiser instanceof DiagramVisualiser) 
			activeVisualiser.loadAnalysis();
		else if(activeVisualiser instanceof FIViewVisualiser)
			((FIViewVisualiser)activeVisualiser).overlayNodes(dataOverlay);
	}
	
	/**
	 * Set isHit used in analysis for overlay in each diagram object
	 */
	private void setIsHitValues() {
		MapSet<String, GraphObject> map = super.context.getContent().getIdentifierMap();
		if(dataOverlay !=null && dataOverlay.getUniprotToEntitiesMap() != null) {
			dataOverlay.getUniprotToEntitiesMap().keySet().forEach((key) ->{
				Set<GraphObject> elements = map.getElements(key);
				if(elements == null) return;
				for(GraphObject graphObject: elements) {
					if(graphObject instanceof GraphPhysicalEntity) {
						GraphPhysicalEntity pe = (GraphPhysicalEntity) graphObject;
						pe.setIsHit(key, getDataOverlayValues(key));
					}
				}
			});
		}
	}
	
	/**
	 * Used to get the correct list of expressions for each graphObject.
	 * This works for data sets with and without tissue specificity.
	 * @param identifier
	 * @return
	 */
	private List<Double> getDataOverlayValues(String identifier){
		int	index = identifier.indexOf("-");	
		if (index > 0)
			identifier = identifier.substring(0, index);
		List<Double> result = new ArrayList<>();
		List<DataOverlayEntity> entities = dataOverlay.getUniprotToEntitiesMap().get(identifier);
		while(result.size()<dataOverlay.getTissueTypes().size()) result.add(null);
		if(entities != null) {
			for(DataOverlayEntity entity : entities) {
				if(dataOverlay.getTissueTypes().size() != 0)	//if else for data sets that are not tissue specific
					result.set(dataOverlay.getTissueTypes().indexOf(entity.getTissue()), entity.getValue());
				else								
					result.add(entity.getValue());
			}
			return result;
		}
		return result;
	}

	@Override
	public void onOverlayDataReset(OverlayDataResetEvent event) {
		renderOverlays = false;
		this.dataOverlay = null;
		clearAnalysisOverlay();
		context.setDialogMap(new HashMap<>());
		if(activeVisualiser instanceof DiagramVisualiser)
			activeVisualiser.loadAnalysis();
		else if(activeVisualiser instanceof FIViewVisualiser)
			((FIViewVisualiser)activeVisualiser).overlayNodes(null);
	}
	
	/**
	 * Removes all expression values from Identifier map during reset overlay so previous overlays
	 * won't interfere with future ones.
	 */
	private void clearAnalysisOverlay() {
		MapSet<String, GraphObject> map = super.context.getContent().getIdentifierMap();
			map.keySet().forEach((key) ->{
			Set<GraphObject> elements = map.getElements(key);
			if(elements == null) return;
			for(GraphObject graphObject: elements) {
				if(graphObject instanceof GraphPhysicalEntity) {
					GraphPhysicalEntity pe = (GraphPhysicalEntity) graphObject;
					pe.resetHit();
				}
			}
		});
	}

	@Override
	public void onDataOverlayColumnChanged(DataOverlayColumnChangedEvent event) {
		this.dataOverlay.setColumn(event.getColumn());
		context.setDialogMap(new HashMap<>());
		if(activeVisualiser instanceof DiagramVisualiser) 
			activeVisualiser.loadAnalysis();
		else if(activeVisualiser instanceof FIViewVisualiser)
			((FIViewVisualiser)activeVisualiser).overlayNodes(dataOverlay);
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
