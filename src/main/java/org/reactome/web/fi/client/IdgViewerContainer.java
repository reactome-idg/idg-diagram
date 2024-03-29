package org.reactome.web.fi.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reactome.web.diagram.client.ViewerContainer;
import org.reactome.web.diagram.client.visualisers.Visualiser;
import org.reactome.web.diagram.client.visualisers.diagram.DiagramVisualiser;
import org.reactome.web.diagram.client.visualisers.ehld.SVGVisualiser;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.content.Content;
import org.reactome.web.diagram.data.graph.model.GraphEntityWithAccessionedSequence;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.graph.model.GraphProteinDrug;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.events.AnalysisResetEvent;
import org.reactome.web.diagram.events.DiagramObjectsFlaggedEvent;
import org.reactome.web.diagram.events.RenderOtherDataEvent;
import org.reactome.web.diagram.handlers.RenderOtherDataHandler;
import org.reactome.web.diagram.legends.FlaggedItemsControl;
import org.reactome.web.diagram.util.MapSet;
import org.reactome.web.fi.client.visualisers.OverlayDataHandler;
import org.reactome.web.fi.client.visualisers.diagram.renderers.ContinuousDataOverlayRenderer;
import org.reactome.web.fi.client.visualisers.diagram.renderers.DiscreteDataOverlayRenderer;
import org.reactome.web.fi.client.visualisers.diagram.renderers.decorators.DrugTargetRenderer;
import org.reactome.web.fi.client.visualisers.diagram.renderers.decorators.PairwiseInteractorRenderer;
import org.reactome.web.fi.client.visualisers.fiview.FIViewVisualizer;
import org.reactome.web.fi.client.visualisers.fiview.panels.SearchPanel;
import org.reactome.web.fi.common.IDGIconButton;
import org.reactome.web.fi.data.loader.PairwiseInfoService;
import org.reactome.web.fi.data.manager.StateTokenHelper;
import org.reactome.web.fi.data.overlay.model.DataOverlayProperties;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayObject;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayProperties;
import org.reactome.web.fi.events.CytoscapeToggledEvent;
import org.reactome.web.fi.events.DataOverlayColumnChangedEvent;
import org.reactome.web.fi.events.DrugTargetsLoadedEvent;
import org.reactome.web.fi.events.DrugTargetsRequestedEvent;
import org.reactome.web.fi.events.FIDiagramObjectsFlaggedEvent;
import org.reactome.web.fi.events.OverlayDataLoadedEvent;
import org.reactome.web.fi.events.OverlayRequestedEvent;
import org.reactome.web.fi.events.PairwiseCountsRequestedEvent;
import org.reactome.web.fi.events.PairwiseInteractorsResetEvent;
import org.reactome.web.fi.events.PairwiseNumbersLoadedEvent;
import org.reactome.web.fi.events.RequestPairwiseCountsEvent;
import org.reactome.web.fi.events.OverlayDataResetEvent;
import org.reactome.web.fi.events.MakeOverlayRequestEvent;
import org.reactome.web.fi.handlers.OverlayDataLoadedHandler;
import org.reactome.web.fi.handlers.OverlayDataResetHandler;
import org.reactome.web.fi.handlers.PairwiseInteractorsResetHandler;
import org.reactome.web.fi.handlers.PairwiseNumbersLoadedHandler;
import org.reactome.web.fi.handlers.RequestPairwiseCountsHandler;
import org.reactome.web.fi.handlers.DataOverlayColumnChangedHandler;
import org.reactome.web.fi.handlers.DrugTargetsLoadedHandler;
import org.reactome.web.fi.handlers.MakeOverlayRequestHandler;
import org.reactome.web.fi.legends.FlaggedInteractorSetLegend;
import org.reactome.web.fi.legends.IDGFlaggedItemsControl;
import org.reactome.web.fi.legends.IDGFlaggingPanel;
import org.reactome.web.fi.legends.OverlayColourLegend;
import org.reactome.web.fi.legends.OverlayControlLegend;
import org.reactome.web.fi.legends.PairwiseControlLegend;
import org.reactome.web.fi.messages.CytoscapeViewLoadingMessage;
import org.reactome.web.fi.messages.NoFIsAvailableMessage;
import org.reactome.web.fi.model.DataOverlay;
import org.reactome.web.fi.model.DataOverlayEntity;
import org.reactome.web.fi.tools.overlay.OverlayLauncherDisplay;
import org.reactome.web.fi.tools.popup.IDGPopupFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.History;

/**
 * 
 * @author brunsont
 *
 */
public class IdgViewerContainer extends ViewerContainer implements RenderOtherDataHandler,
		OverlayDataLoadedHandler, OverlayDataResetHandler, MakeOverlayRequestHandler, DataOverlayColumnChangedHandler, RequestPairwiseCountsHandler, PairwiseInteractorsResetHandler,
		PairwiseNumbersLoadedHandler, DrugTargetsLoadedHandler {

	private IDGIconButton fiviewButton;
	private IDGIconButton diagramButton;
	private SearchPanel fiSearchPanel;
	private IDGIconButton fiSettingsButton;
	private FIViewVisualizer fIViewVisualizer;
	private IDGIconButton overlayButton;
	private OverlayColourLegend overlayColourLegend;
	private OverlayControlLegend overlayControlLegend;
	private PairwiseControlLegend pairwiseControlLegend;
	private OverlayLauncherDisplay overlayLauncher;

	private DataOverlay dataOverlay;
	private DataOverlay targetLevelOverlay;

	private PairwiseInteractorRenderer pairwiseDecoratorRenderer;
	private DrugTargetRenderer drugTargetRenderer;

	private DataOverlayProperties lastOverlayProperties = null;

	public IdgViewerContainer(EventBus eventBus) {
		super(eventBus);

		initHandlers();
		PairwiseInfoService.loadUniprotToGeneMap();
	}

	private void initHandlers() {
		eventBus.addHandler(RenderOtherDataEvent.TYPE, this);
		eventBus.addHandler(OverlayDataLoadedEvent.TYPE, this);
		eventBus.addHandler(OverlayDataResetEvent.TYPE, this);
		eventBus.addHandler(MakeOverlayRequestEvent.TYPE, this);
		eventBus.addHandler(DataOverlayColumnChangedEvent.TYPE, this);
		eventBus.addHandler(RequestPairwiseCountsEvent.TYPE, this);
		eventBus.addHandler(PairwiseInteractorsResetEvent.TYPE, this);
		eventBus.addHandler(PairwiseNumbersLoadedEvent.TYPE, this);
		eventBus.addHandler(DrugTargetsLoadedEvent.TYPE, this);
	}

	@Override
	protected void initialise() {
		overlayColourLegend = new OverlayColourLegend(eventBus);

		super.initialise();

		// this block used to disable settings panel not used in the idg portal for
		// reactome.
		super.rightContainerPanel.remove(hideableContainerPanel);
		super.rightContainerPanel.getElement().getStyle().setRight(0, Unit.PX);

		// Add overlayColourLegend to right container panel
		super.rightContainerPanel.add(overlayColourLegend);

		this.add(new CytoscapeViewLoadingMessage(eventBus));
		this.add(new NoFIsAvailableMessage(eventBus));
		fiviewButton = new IDGIconButton(IDGRESOURCES.cytoscapeIcon(), IDGRESOURCES.getCSS().cytoscape(),
				"Cytoscape View");
		diagramButton = new IDGIconButton(IDGRESOURCES.diagramIcon(), IDGRESOURCES.getCSS().diagram(), "Diagram View");
		diagramButton.setVisible(false);
		overlayButton = new IDGIconButton(IDGRESOURCES.overlayIcon(), IDGRESOURCES.getCSS().cytoscape(),
				"Select an Overlay");
		fiSettingsButton = new IDGIconButton(IDGRESOURCES.gear(), IDGRESOURCES.getCSS().settings(), "Configure FIView");
		fiSettingsButton.setVisible(false);
		overlayLauncher = new OverlayLauncherDisplay(eventBus);

		super.leftTopLauncher.insert(fiSearchPanel = new SearchPanel(eventBus), 1);
		fiSearchPanel.setVisible(false);
		// adds diagramButton and fiviewButton. sets fiview button as default to show
		super.leftTopLauncher.getMainControlPanel().add(diagramButton);
		super.leftTopLauncher.getMainControlPanel().add(fiviewButton);
		super.leftTopLauncher.getMainControlPanel().add(overlayButton);
		super.leftTopLauncher.getMainControlPanel().add(fiSettingsButton);
		overlayControlLegend = new OverlayControlLegend(eventBus);
		super.bottomContainerPanel.add(overlayControlLegend);
		pairwiseControlLegend = new PairwiseControlLegend(eventBus);
		super.bottomContainerPanel.add(pairwiseControlLegend);
		super.bottomContainerPanel.remove(super.interactorsControl);
		this.add(overlayLauncher);

		// replace FlaggedItemsControl with IDG specific one
		for (int i = 0; i < bottomContainerPanel.getWidgetCount(); i++) {
			if (bottomContainerPanel.getWidget(i) instanceof FlaggedItemsControl)
				bottomContainerPanel.remove(i);
		}
		bottomContainerPanel.add(new IDGFlaggingPanel(eventBus));

		bind();

		// create custom renderers for rendering over or re-rendering pathway diagrams.
		// Each custom renderer needs to access the decorator renderer
		pairwiseDecoratorRenderer = new PairwiseInteractorRenderer(eventBus);
		drugTargetRenderer = new DrugTargetRenderer(eventBus);
		OverlayDataHandler.getHandler().registerHelper(
				new DiscreteDataOverlayRenderer(eventBus, pairwiseDecoratorRenderer, drugTargetRenderer));
		OverlayDataHandler.getHandler().registerHelper(
				new ContinuousDataOverlayRenderer(eventBus, pairwiseDecoratorRenderer, drugTargetRenderer));
	}

	@Override
	protected void addExternalVisualisers() {
		fIViewVisualizer = new FIViewVisualizer(eventBus);
		super.add(fIViewVisualizer);
	}

	@Override
	protected void setActiveVisualiser(Context context) {
		hideContextMenus();
		hideButtons();
		StateTokenHelper helper = new StateTokenHelper();
		boolean isCytoscapeView = helper.buildTokenMap(History.getToken()).containsKey("FIVIZ");
		if (context.getContent().getType() == Content.Type.DIAGRAM && isCytoscapeView) {
			for (Visualiser vis : visualisers.values()) {
				vis.asWidget().setVisible(false);
			}
			fIViewVisualizer.asWidget().setVisible(true);
			showHideTopRightLauncherPanel(false);
			showFIVizualizerButtons();
			activeVisualiser = fIViewVisualizer;
			return;
		} else if (context.getContent().getType() == Content.Type.DIAGRAM && !isCytoscapeView) {
			showDiagramButtons();
			super.setActiveVisualiser(context);
			showHideTopRightLauncherPanel(true);
		}
		super.setActiveVisualiser(context);
		showHideTopRightLauncherPanel(true);
	}
	
	private void showHideTopRightLauncherPanel(boolean visible) {
		super.rightTopLauncherPanel.setVisible(visible);
	}

	private void hideContextMenus() {
		fIViewVisualizer.clearNodeContextMap();
		context.setDialogMap(new HashMap<>());
	}

	@Override
	public void contentLoaded(Context context) {
		super.contentLoaded(context);

		if (context.getContent().getType() == Content.Type.SVG)
			return;

		// check if overlay should be loaded and if so, load new Overlay data
		if (dataOverlay != null) {
			eventBus.fireEventFromSource(new OverlayDataResetEvent(), this);
			eventBus.fireEventFromSource(new MakeOverlayRequestEvent(this.lastOverlayProperties), this);
		} else {
			eventBus.fireEventFromSource(new MakeOverlayRequestEvent(getTargetLevelProperties()), this);
		}

		if (IDGPopupFactory.get().getCurrentPairwiseProperties() != null
				&& IDGPopupFactory.get().getCurrentPairwiseProperties().size() > 0) {
			eventBus.fireEventFromSource(
					new RequestPairwiseCountsEvent(IDGPopupFactory.get().getCurrentPairwiseProperties()), this);
		}

		loadDrugActivities();
	}

	@Override
	public void contentRequested() {
		drugTargetRenderer.contentRequested();
		super.contentRequested();
	}

	private void loadDrugActivities() {
		eventBus.fireEventFromSource(new DrugTargetsRequestedEvent(String.join(",", collectAllDiagramUniprots())),
				this);
	}

	private DataOverlayProperties getTargetLevelProperties() {
		DataOverlayProperties result = new DataOverlayProperties("String", null, null, null,
				"Target Development Level");
		return result;
	}

	private void bind() {
		fiviewButton.addClickHandler(e -> cytoscapeButtonPressed());
		diagramButton.addClickHandler(e -> cytoscapeButtonPressed());
		overlayButton.addClickHandler(e -> toggleOverlayPanel());
		fiSettingsButton.addClickHandler(e -> onFISettingsButtonClicked());
	}

	private void onFISettingsButtonClicked() {
		fIViewVisualizer.openSettingsPopup(fiSettingsButton.getAbsoluteLeft(), fiSettingsButton.getAbsoluteTop());
	}

	private void toggleOverlayPanel() {
		overlayLauncher.center();
		overlayLauncher.show();
	}

	public void flagObjects(String term, List<Long> pes, boolean includeInteractors) {
		if (this.activeVisualiser instanceof DiagramVisualiser) {
			flagDiagramObjects(term, pes, includeInteractors);
			((DiagramVisualiser) activeVisualiser).forceDraw();
		} else if (this.activeVisualiser instanceof SVGVisualiser) {
			flagDiagramObjects(term, pes, includeInteractors);
		} else if (this.activeVisualiser instanceof FIViewVisualizer)
			flagFIObjects(term, includeInteractors);
	}

	private void flagFIObjects(String term, boolean includeInteractors) {
		List<String> nodesToHighlight = new ArrayList<>();
		List<String> interactors = IDGPopupFactory.get().getFlagInteractors();
		Set<String> uniprots = this.context.getContent().getIdentifierMap().keySet();
		uniprots.forEach(uniprot -> {
			if (interactors.contains(uniprot))
				nodesToHighlight.add(uniprot);
		});
		eventBus.fireEventFromSource(
				new FIDiagramObjectsFlaggedEvent(term, includeInteractors, nodesToHighlight, false), this);
	}

	private void flagDiagramObjects(String term, List<Long> pes, boolean includeInteractors) {
		Set<DiagramObject> flaggedObjects = new HashSet<>();
		context.getContent().getDiagramObjects().forEach(diagramObject -> {
			if (pes.contains(diagramObject.getReactomeId()))
				flaggedObjects.add(diagramObject);
		});

		eventBus.fireEventFromSource(new DiagramObjectsFlaggedEvent(term, includeInteractors, flaggedObjects, false),
				this);
	}

	@Override
	public void onMakeOverlayRequest(MakeOverlayRequestEvent event) {

		// set lastOverlayProperties in case diagram is changed before reset
		this.lastOverlayProperties = event.getDataOverlayProperties();

		// can't have an overlay and Analysis at the same time
		if (context.getAnalysisStatus() != null)
			eventBus.fireEventFromSource(new AnalysisResetEvent(), this);

		eventBus.fireEventFromSource(new OverlayDataResetEvent(), this);
		event.getDataOverlayProperties().setUniprots(String.join(",", collectAllDiagramUniprots()));
		event.getDataOverlayProperties().setPathwayStableId(context.getContent().getStableId());
		eventBus.fireEventFromSource(new OverlayRequestedEvent(event.getDataOverlayProperties()), this);
	}

	@Override
	public void onRequestPairwiseCountsHandeler(RequestPairwiseCountsEvent event) {
		PairwiseOverlayProperties props = new PairwiseOverlayProperties(event.getPairwiseOverlayObjects(),
				collectAllDiagramUniprots());

		eventBus.fireEventFromSource(new PairwiseCountsRequestedEvent(props), this);
	}

	@Override
	public void onPairwiseInteractorsReset(PairwiseInteractorsResetEvent event) {
		IDGPopupFactory.get().closeAllPopups();
		IDGPopupFactory.get().setCurrentPairwiseProperties(new ArrayList<PairwiseOverlayObject>());
		IDGPopupFactory.get().setPairwiseNumberEntities(new ArrayList<>());
		IDGPopupFactory.get().setGeneToTotalMap(new HashMap<>());

		if (activeVisualiser == fIViewVisualizer)
			return;

		for (DiagramObject item : context.getContent().getDiagramObjects()) {
			if (item instanceof Node) {
				Node node = (Node) item;
				node.setInteractorsSummary(null);
			}
		}
		activeVisualiser.resetAnalysis(); // trick to get diagram to re-render now without decorators
	}

	/**
	 * Collects uniprots for all participants in a diagram or FIView
	 * 
	 * @return
	 */
	private List<String> collectAllDiagramUniprots() {
		Set<String> identifiers = new HashSet<>();

		// get identifiers from identifierMap if active visualizer is FIViewVisualizer
		if (activeVisualiser instanceof FIViewVisualizer) {
			identifiers = context.getContent().getIdentifierMap().keySet();
			return new ArrayList<String>(identifiers);
		}

		// if activeVisualiser is DiagramVisualiser
		// iterate over all diagram objects in a diagram
		for (DiagramObject diagramObject : context.getContent().getDiagramObjects()) {

			// Get graph object of each diagramObject, check if its a GraphPhysicalEntity,
			// and get each participant if so. Then add identifier of each participant
			// to a set of identifiers.
			GraphObject graphObject = diagramObject.getGraphObject();
			if (graphObject instanceof GraphPhysicalEntity) {
				GraphPhysicalEntity pe = (GraphPhysicalEntity) graphObject;
				for (GraphPhysicalEntity participant : pe.getParticipants()) {
					if (participant instanceof GraphEntityWithAccessionedSequence
							|| participant instanceof GraphProteinDrug) {
						String identifier = participant.getIdentifier();
						if (identifier.contains("-"))
							identifier = identifier.substring(0, identifier.indexOf("-"));
						else if (identifier.contains("ENSG") || identifier.contains("ENST")) {
							for (Map.Entry<String, String> entry : PairwiseInfoService.getUniprotToGeneMap()
									.entrySet()) { // Iterate over map. Check value vs. display name
								if (participant.getDisplayName().contains(entry.getValue())) { // If equal, replace with
																								// key (uniprot)
									identifier = entry.getKey();
									break;
								}
							}
						}
						identifiers.add(identifier);
					}
				}
			}
		}
		return new ArrayList<String>(identifiers);
	}

	private void cytoscapeButtonPressed() {
//		CytoscapeViewFlag.toggleCytoscapeViewFlag();

		// decide if adding or removing FIVIZ from url bar
		StateTokenHelper helper = new StateTokenHelper();
		Map<String, String> tokenMap = helper.buildTokenMap(History.getToken());
		if (tokenMap.containsKey("FIVIZ"))
			tokenMap.remove("FIVIZ");
		else
			tokenMap.put("FIVIZ", "FIVIZ");
		History.newItem(helper.buildToken(tokenMap));

		eventBus.fireEventFromSource(new CytoscapeToggledEvent(getContext()), this);

	}

	/**
	 * Shows buttons to be present on DiagramVisualizer
	 */
	private void showDiagramButtons() {
		super.leftTopLauncher.getSearchPanel().setVisible(true);
		fiviewButton.setVisible(true);
		showOverlayButton();
	}

	/**
	 * shows buttons to be present on FiViewVisualizer
	 */
	private void showFIVizualizerButtons() {
		fiSearchPanel.setVisible(true);
		diagramButton.setVisible(true);
		fiSettingsButton.setVisible(true);
		showOverlayButton();
	}

	private void showOverlayButton() {
		overlayButton.setVisible(true);
	}

	/**
	 * hides fiview, diagram, and overlay button. Also hides overlayDialogPanel
	 */
	private void hideButtons() {
		super.leftTopLauncher.getSearchPanel().setVisible(false);
		fiSearchPanel.setVisible(false);
		fiviewButton.setVisible(false);
		diagramButton.setVisible(false);
		overlayButton.setVisible(false);
		fiSettingsButton.setVisible(false);
		overlayLauncher.hide();
	}

	/**
	 * Directs rendering of data on diagrams after the base diagram is rendered
	 */
	@Override
	public void onRenderOtherData(RenderOtherDataEvent event) {
		if (this.dataOverlay == null)
			return;

		OverlayDataHandler.getHandler().overlayData(event.getItems(), event.getOverlay(), context,
				event.getRendererManager(), event.getOverlayContext());
	}

	@Override
	public void onOverlayDataLoaded(OverlayDataLoadedEvent event) {
		if (event.getDataOverlay().getOverlayProperties().geteType() == "Target Development Level") {
			this.targetLevelOverlay = event.getDataOverlay();
		}
		this.overlayLauncher.hide();
		this.dataOverlay = event.getDataOverlay();
		IDGPopupFactory.get().setDataOverlayProperties(event.getDataOverlay().getOverlayProperties());

		setIsHitValues();

		if (activeVisualiser instanceof DiagramVisualiser)
			activeVisualiser.loadAnalysis();
		else if (activeVisualiser instanceof FIViewVisualizer)
			((FIViewVisualizer) activeVisualiser).overlayNodes(dataOverlay);

		if (context.getDialogMap() != null)
			context.getDialogMap().values().forEach(dialog -> {
				dialog.redraw();
			});

	}

	/**
	 * Set isHit used in analysis for overlay in each diagram object
	 */
	private void setIsHitValues() {
		MapSet<String, GraphObject> map = super.context.getContent().getIdentifierMap();
		if (dataOverlay != null && dataOverlay.getUniprotToEntitiesMap() != null) {
			dataOverlay.getUniprotToEntitiesMap().keySet().forEach((key) -> {
				Set<GraphObject> elements = map.getElements(key);
				if (elements == null)
					return;
				for (GraphObject graphObject : elements) {
					if (graphObject instanceof GraphPhysicalEntity) {
						GraphPhysicalEntity pe = (GraphPhysicalEntity) graphObject;
						pe.setIsHit(key, getDataOverlayValues(key));
					}
				}
			});
		}
	}

	/**
	 * Used to get the correct list of expressions for each graphObject. This works
	 * for data sets with and without tissue specificity.
	 * 
	 * @param identifier
	 * @return
	 */
	private List<Double> getDataOverlayValues(String identifier) {
		int index = identifier.indexOf("-");
		if (index > 0)
			identifier = identifier.substring(0, index);
		List<Double> result = new ArrayList<>();
		List<DataOverlayEntity> entities = dataOverlay.getUniprotToEntitiesMap().get(identifier);
		while (result.size() < dataOverlay.getTissueTypes().size())
			result.add(null);
		if (entities != null) {
			for (DataOverlayEntity entity : entities) {
				if (dataOverlay.getTissueTypes().size() != 0) // if else for data sets that are not tissue specific
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
		this.dataOverlay = null;
		clearAnalysisOverlay();

		if (context.getDialogMap() != null)
			context.getDialogMap().values().forEach(dialog -> dialog.redraw());

		if (activeVisualiser instanceof DiagramVisualiser)
			activeVisualiser.loadAnalysis();
		else if (activeVisualiser instanceof FIViewVisualizer)
			((FIViewVisualizer) activeVisualiser).overlayNodes(null);

		if (this.targetLevelOverlay != null && event.getSource() instanceof OverlayControlLegend)
			this.eventBus.fireEventFromSource(new MakeOverlayRequestEvent(getTargetLevelProperties()), this);
	}

	/**
	 * Removes all expression values from Identifier map during reset overlay so
	 * previous overlays won't interfere with future ones.
	 */
	private void clearAnalysisOverlay() {
		MapSet<String, GraphObject> map = super.context.getContent().getIdentifierMap();
		map.keySet().forEach((key) -> {
			Set<GraphObject> elements = map.getElements(key);
			if (elements == null)
				return;
			for (GraphObject graphObject : elements) {
				if (graphObject instanceof GraphPhysicalEntity) {
					GraphPhysicalEntity pe = (GraphPhysicalEntity) graphObject;
					pe.resetHit();
				}
			}
		});
	}

	@Override
	public void onDataOverlayColumnChanged(DataOverlayColumnChangedEvent event) {
		this.dataOverlay.setColumn(event.getColumn());
		IDGPopupFactory.get().setOverlayColumn(event.getColumn());
		if (activeVisualiser instanceof DiagramVisualiser)
			activeVisualiser.loadAnalysis();
		else if (activeVisualiser instanceof FIViewVisualizer)
			((FIViewVisualizer) activeVisualiser).overlayNodes(dataOverlay);
	}

	@Override
	public void onPairwiseNumbersLoaded(PairwiseNumbersLoadedEvent event) {
		StateTokenHelper helper = new StateTokenHelper();
		if(context.getContent().getType() == Content.Type.DIAGRAM && !helper.buildTokenMap(History.getToken()).containsKey("FIVIZ"))
			pairwiseDecoratorRenderer.onPairwiseNumbersLoaded(event);
	}

	@Override
	public void onDrugTargetsLoaded(DrugTargetsLoadedEvent event) {
		if (activeVisualiser instanceof DiagramVisualiser) {
			drugTargetRenderer.onDrugTargetsLoaded(event);
			((DiagramVisualiser) activeVisualiser).forceDraw();
		}
		if (activeVisualiser instanceof FIViewVisualizer) {
			((FIViewVisualizer) activeVisualiser).setDrugInteractions(event.getDrugTargets());
		}
	}

	@Override
	public void onResize() {
		super.onResize();
		fIViewVisualizer.setSize(this.getOffsetWidth(), this.getOffsetHeight());
	}

	public Visualiser getActiveVisualizer() {
		return this.activeVisualiser;
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

		@Source("images/gear.png")
		ImageResource gear();
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

		String settings();
	}
}
