package org.reactome.web.fi.client.visualisers.fiview;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.fi.events.DataOverlayColumnChangedEvent;
import org.reactome.web.fi.handlers.DataOverlayColumnChangedHandler;
import org.reactome.web.fi.model.DataOverlay;
import org.reactome.web.fi.model.DataOverlayEntity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

/**
 * 
 * @author brunsont
 *
 */
public class NodeContextPanel extends DialogBox implements DataOverlayColumnChangedHandler{
	
	private EventBus eventBus;
	
	private FlowPanel main;
	private FlowPanel overlayValue;
	private Label overlayValueLabel;
	
	private boolean pinned = false;
	private PwpButton pin;
	
	private final String PROTEIN_URL = "https://www.uniprot.org/uniprot/";
	private final String GENE_URL = "http://www.ensembl.org/Homo_sapiens/geneview?gene=";
	
	private String name;
	private String id;
	Map<Integer, Double> colToValueMap;
	private List<String> legendTypes = null;
	private int column;
	private boolean discrete;
	private String eType;
	
	/**
	 * Contstructor for when there is no overlay
	 * @param name
	 * @param id
	 */
	public NodeContextPanel(EventBus eventBus, String name, String id) {
		super();
		this.eventBus = eventBus;
		this.name  = name;
		this.id = id;
		initPanel();
	}
	
	/**
	 * Constructor to use if dataOverlay is present
	 * @param name
	 * @param id
	 * @param overlay
	 */
	public NodeContextPanel(EventBus eventBus, String name, String id, DataOverlay overlay) {
		this.eventBus = eventBus;
		this.name = name;
		this.id = id;
		this.column = 0;
		this.legendTypes = overlay.getLegendTypes();
		this.discrete = overlay.isDiscrete();
		this.eType = overlay.getEType();
		
		colToValueMap = new HashMap<>();
		if(overlay.getTissueTypes() == null || overlay.getTissueTypes().size() < 1)
			colToValueMap.put(0, overlay.getUniprotToEntitiesMap().get(id).get(0).getValue());
		else {
			overlay.getUniprotToEntitiesMap().get(id).forEach((x) -> {
				colToValueMap.put(overlay.getTissueTypes().indexOf(x.getTissue()), x.getValue());
			});
		}
		
		initPanel();
		eventBus.addHandler(DataOverlayColumnChangedEvent.TYPE, this);
		updateOverlayValue();
	}
	
	private void initPanel() {
		setStyleName(NODECONTEXTRESOURCES.getCSS().nodePopup());
		setAutoHideEnabled(true);
		setModal(false);
		
		main = new FlowPanel();
		main.setStyleName(NODECONTEXTRESOURCES.getCSS().mainPanel());
		
		main.add(pin = new PwpButton("Keeps the panel visible", NODECONTEXTRESOURCES.getCSS().pin(), e -> pinHandler()));
		main.add(new PwpButton("Close", NODECONTEXTRESOURCES.getCSS().close(), e -> closeHandler()));
		
		main.add(getInfoPanel(name, id));
		main.add(overlayValue = new FlowPanel());
		overlayValue.add(overlayValueLabel = new Label());
		overlayValueLabel.setStyleName(NODECONTEXTRESOURCES.getCSS().label());
		
		setTitlePanel();
		setWidget(main);
		show();
	}

	private void updateOverlayValue() {
		Double expression = colToValueMap.get(column);
		if(!discrete) 
			overlayValueLabel.setText(eType + ": " + expression);
		else 
			overlayValueLabel.setText(eType + ": " + legendTypes.get(expression.intValue()));
	}

	private void closeHandler() {
		this.pinned = false;
		pin.setStyleName(NODECONTEXTRESOURCES.getCSS().pin());
		hide();
	}

	private void pinHandler() {
		this.pinned = !this.pinned;
		
		if(this.pinned)
			pin.setStyleName(NODECONTEXTRESOURCES.getCSS().pinActive());
		else
			pin.setStyleName(NODECONTEXTRESOURCES.getCSS().pin());
			
	}

	private void setTitlePanel() {
		FlowPanel fp = new FlowPanel();
		Image img = new Image(NODECONTEXTRESOURCES.entity());
		fp.add(img);
		
		InlineLabel title = new InlineLabel(this.name);
		fp.add(title);
		
		SafeHtml safe = SafeHtmlUtils.fromTrustedString(fp.toString());
		getCaption().setHTML(safe);
		getCaption().asWidget().setStyleName(NODECONTEXTRESOURCES.getCSS().header());
	}

	public FlowPanel getInfoPanel(String name, String id) {

		FlowPanel panel = new FlowPanel();
		
		Label lbl = new Label("Gene Symbol: " +  name);
		lbl.setStyleName(NODECONTEXTRESOURCES.getCSS().label());
		panel.add(lbl);

		if(!id.contains("ENSG")) {
			String link = PROTEIN_URL + id;
			Anchor linkAnchor = new Anchor(new SafeHtmlBuilder()
					.appendEscapedLines("Uniprot: " + id).toSafeHtml(),
					link, "_blank");
			linkAnchor.setStyleName(NODECONTEXTRESOURCES.getCSS().linkAnchor());
			panel.add(linkAnchor);
		}
		else if(id.contains("ENSG")) {
			String link = GENE_URL + id;
			Anchor linkAnchor = new Anchor(new SafeHtmlBuilder().appendEscapedLines("Ensembl: " + id).toSafeHtml(),
					link, "_blank");
			linkAnchor.setStyleName(NODECONTEXTRESOURCES.getCSS().linkAnchor());
			panel.add(linkAnchor);
		}
		return panel;
	}
	
	/**
	 * Modeled after ContextDialogPanel in base diagram project
	 */
	@Override
	public void hide(boolean autoClosed) {
		if(autoClosed && !this.pinned)
			super.hide(autoClosed);
		else if(!autoClosed)
			super.hide(autoClosed);
	}
	
	@Override
	public void onDataOverlayColumnChanged(DataOverlayColumnChangedEvent event) {
		this.column = event.getColumn();
		updateOverlayValue();
	}
	
	public static NodeContextResources NODECONTEXTRESOURCES;
	static {
		NODECONTEXTRESOURCES = GWT.create(NodeContextResources.class);
		NODECONTEXTRESOURCES.getCSS().ensureInjected();
	}
	
	public interface NodeContextResources extends ClientBundle{
		@Source(ResourceCSS.CSS)
		ResourceCSS getCSS();
		
		@Source("images/pin_clicked.png")
        ImageResource pinClicked();

        @Source("images/pin_hovered.png")
        ImageResource pinHovered();

        @Source("images/pin_normal.png")
        ImageResource pinNormal();

        @Source("images/close_clicked.png")
        ImageResource closeClicked();

        @Source("images/close_hovered.png")
        ImageResource closeHovered();

        @Source("images/close_normal.png")
        ImageResource closeNormal();
        
        @Source("images/entity.gif")
        ImageResource entity();
	}
	
	@CssResource.ImportedWithPrefix("idgDiagram-NodeContextPanel")
	public interface ResourceCSS extends CssResource{
		String CSS = "org/reactome/web/fi/client/visualisers/fiview/NodeContextPanel.css";
		
		String nodePopup();
		
		String mainPanel();

		String label();
		
		String linkAnchor();
		
		String header();
		
		String pin();
		
		String close();
		
		String pinActive();
	}
}
