package org.reactome.web.fi.tools.export;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;

import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.fi.common.CommonButton;
import org.reactome.web.fi.data.loader.OverlayLoader;
import org.reactome.web.fi.tools.overlay.pairwise.model.PairwiseTableEntity;
import org.reactome.web.gwtCytoscapeJs.util.Console;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

public class ExportPairwiseRelationshipsPanel extends PopupPanel{

	private String popupId;
	private List<PairwiseTableEntity> exportEntities;
	
	private FlowPanel main;
	private CheckBox targetDevLevel;
	private CheckBox expressionData;
	
	public ExportPairwiseRelationshipsPanel(String popupId, List<PairwiseTableEntity> exportEntities) {
		this.popupId = popupId;
		this.exportEntities = exportEntities;
		initPanel();
	}

	private void initPanel() {
		setAutoHideEnabled(true);
		setGlassEnabled(true);
		setModal(true);
		setAnimationEnabled(true);
		this.setStyleName(RESOURCES.getCSS().popupPanel());

		int width = (int) Math.round(Window.getClientWidth() * .25);
		int height = (int) Math.round(Window.getClientHeight() * .25);
		this.setWidth(width + "px");
		this.setHeight(height + "px");
		
		main = new FlowPanel();
		main.setStyleName(RESOURCES.getCSS().outerPanel());
		main.add(setTitlePanel());
		main.add(getMainPanel());
		
		CommonButton export = new CommonButton("Export", e -> onExportButtonClicked());
		export.setStyleName(RESOURCES.getCSS().exportButton());
		main.add(export);
		
		setTitlePanel();
		setWidget(main);
		center();
		show();
	}
	
	private FlowPanel setTitlePanel() {
		FlowPanel fp = new FlowPanel();
		fp.setStyleName(RESOURCES.getCSS().header());
		fp.addStyleName(RESOURCES.getCSS().unselectable());
		InlineLabel title = new InlineLabel("Export: " + popupId);
		fp.add(title);
		fp.add(new PwpButton("Close", RESOURCES.getCSS().close(), e -> ExportPairwiseRelationshipsPanel.this.hide()));
		return fp;
	}

	private FlowPanel getMainPanel() {
		FlowPanel result = new FlowPanel();
		result.setStyleName(RESOURCES.getCSS().mainPanel());
		
		Label optionsLabel = new Label("Select Options to include:");
		optionsLabel.setStyleName(RESOURCES.getCSS().optionsLabel());
		result.add(optionsLabel);
		
		targetDevLevel = new CheckBox("Target Development Levels");
		targetDevLevel.setStyleName(RESOURCES.getCSS().checkBox());
		result.add(targetDevLevel);
		
		expressionData = new CheckBox("Expression Overlay Data");
		expressionData.setStyleName(RESOURCES.getCSS().checkBox());
		result.add(expressionData);
		expressionData.setEnabled(false);
		
		return result;
	}
	
	/**
	 * Runs on export button press
	 * Based on current options, gets extra information needed from server and starts download
	 * download is tab separated data
	 */
	private void onExportButtonClicked() {
		//get all uniprots needed for TCRD server call
		exportWithTargetDevLevel();
	}

	/**
	 * Makes server call and fills tableEntities with Target Dev Levels
	 */
	private void exportWithTargetDevLevel() {
		Set<String> uniprotSet = new HashSet<>();
		for(PairwiseTableEntity entity: exportEntities) {
			uniprotSet.add(entity.getInteractorId());
			uniprotSet.add(entity.getSourceId());
		}
		
		OverlayLoader loader = new OverlayLoader();
		loader.loadMultipleTargetLevelProtein(String.join(",", uniprotSet), new AsyncCallback<Map<String, String>>(){
			@Override
			public void onSuccess(Map<String, String> result) {
				buildExport(result);
			}
			@Override
			public void onFailure(Throwable caught) {
				Console.error(caught);
			}
		});
	}
	
	private void buildExport(Map<String, String> uniprotToTDL) {
		String export = "";
		for(PairwiseTableEntity entity : exportEntities) {
			if(entity.getPosOrNeg() == "solid") continue;
			entity.setSourceTDL(uniprotToTDL.get(entity.getSourceId()));
			entity.setInteractorTDL(uniprotToTDL.get(entity.getInteractorId()));
			export += (entity.toStringForExport() + "\n");
		}
		Anchor download = new Anchor("Download");
		download.getElement().setAttribute("rel", "noindex,nofollow");
		download.getElement().setAttribute("download", export);
	}

	public static Resources RESOURCES;
	static {
		RESOURCES = GWT.create(Resources.class);
		RESOURCES.getCSS().ensureInjected();
	}
	
	public interface Resources extends ClientBundle{
		@Source(ResourceCSS.CSS)
		ResourceCSS getCSS();
		
		@Source("images/close_clicked.png")
        ImageResource closeClicked();

        @Source("images/close_hovered.png")
        ImageResource closeHovered();

        @Source("images/close_normal.png")
        ImageResource closeNormal();
	}
	
	@CssResource.ImportedWithPrefix("idg-exportDialog")
	public interface ResourceCSS extends CssResource{
		String CSS = "org/reactome/web/fi/tools/export/ExportPairwiseRelationshipsPanel.css";
		
		String popupPanel();
		
		String outerPanel();
		
		String close();
		
		String header();
		
		String unselectable();
		
		String mainPanel();
		
		String optionsLabel();
		
		String checkBox();
		
		String exportButton();
	}
	
}
