package org.reactome.web.fi.client;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.reactome.web.analysis.client.model.AnalysisType;
import org.reactome.web.diagram.client.ViewerContainer;
import org.reactome.web.diagram.client.visualisers.Visualiser;
import org.reactome.web.diagram.client.visualisers.diagram.DiagramVisualiser;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.content.Content;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.events.RenderOtherDataEvent;
import org.reactome.web.diagram.handlers.RenderOtherDataHandler;
import org.reactome.web.diagram.util.AdvancedContext2d;
import org.reactome.web.diagram.util.MapSet;
import org.reactome.web.diagram.renderers.helper.ItemsDistribution;
import org.reactome.web.diagram.renderers.helper.RenderType;
import org.reactome.web.diagram.renderers.layout.Renderer;
import org.reactome.web.fi.client.visualisers.OverlayDataHandler;
import org.reactome.web.fi.client.visualisers.diagram.renderers.OverlayColourNode;
import org.reactome.web.fi.client.visualisers.diagram.renderers.OverlayColourProperties;
import org.reactome.web.fi.client.visualisers.diagram.renderers.OverlayColours;
import org.reactome.web.fi.client.visualisers.diagram.renderers.ProteinTargetLevelRenderer;
import org.reactome.web.fi.client.visualisers.fiview.FIViewVisualiser;
import org.reactome.web.fi.common.CytoscapeViewFlag;
import org.reactome.web.fi.common.IDGIconButton;
import org.reactome.web.fi.data.overlay.RawOverlayEntities;
import org.reactome.web.fi.events.CytoscapeToggledEvent;
import org.reactome.web.fi.events.OverlayDataLoadedEvent;
import org.reactome.web.fi.events.OverlayDataRequestedEvent;
import org.reactome.web.fi.handlers.OverlayDataLoadedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Button;

/**
 * 
 * @author brunsont
 *
 */
public class IdgViewerContainer extends ViewerContainer implements RenderOtherDataHandler,
OverlayDataLoadedHandler{

	private IDGIconButton fiviewButton;
	private IDGIconButton diagramButton;
	private FIViewVisualiser fIViewVisualiser;
	private Button targetLevelTest;
	
	private RawOverlayEntities targetLevelEntities;
	
	public IdgViewerContainer(EventBus eventBus) {
		super(eventBus);
		
		eventBus.addHandler(RenderOtherDataEvent.TYPE, this);
		eventBus.addHandler(OverlayDataLoadedEvent.TYPE, this);
	}

	@Override
	protected void initialise() {
		super.initialise();
		
		fiviewButton = new IDGIconButton(IDGRESOURCES.cytoscapeIcon(), IDGRESOURCES.getCSS().cytoscape(), "Cytoscape View");
		diagramButton = new IDGIconButton(IDGRESOURCES.diagramIcon(), IDGRESOURCES.getCSS().diagram(), "Diagram View");
		targetLevelTest = new Button("Test Button");
		
		//adds diagramButton and fiviewButton. sets fiview button as default to show
		super.leftTopLauncher.getMainControlPanel().add(diagramButton);
		super.leftTopLauncher.getMainControlPanel().getWidget(
				super.leftTopLauncher.getMainControlPanel()
				.getWidgetIndex(diagramButton)).setVisible(false);
		super.leftTopLauncher.getMainControlPanel().add(fiviewButton);
		super.leftTopLauncher.getMainControlPanel().add(targetLevelTest);
		
		bind();
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
	}
	
	private void bind() {
		fiviewButton.addClickHandler(e -> cytoscapeButtonPressed());
		diagramButton.addClickHandler(e -> cytoscapeButtonPressed());
		targetLevelTest.addClickHandler(e -> requestOverlayData());
	}
	
	private void requestOverlayData() {
		Set<String> identifiers = null;

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
		eventBus.fireEventFromSource(new OverlayDataRequestedEvent(identifiers), this);
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
		if(this.targetLevelEntities == null)
			return;
		OverlayColours colours = OverlayColours.get();
		OverlayColourProperties tLevel = colours.getColours("targetlevel");
		
		OverlayDataHandler.getHandler().registerHelper(new ProteinTargetLevelRenderer());
		OverlayDataHandler.getHandler().overlayData(event.getItems(), event.getCtx(), context, event.getRendererManager(), this.targetLevelEntities);
	}
	
	@Override
	public void onTargetLevelDataLoaded(OverlayDataLoadedEvent event) {
		this.targetLevelEntities = event.getEntities();
		if(activeVisualiser instanceof DiagramVisualiser) 
			activeVisualiser.loadAnalysis();
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
