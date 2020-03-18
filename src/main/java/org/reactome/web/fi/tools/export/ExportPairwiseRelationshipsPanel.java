package org.reactome.web.fi.tools.export;

import java.util.List;

import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.fi.common.CommonButton;
import org.reactome.web.fi.tools.overlay.pairwise.model.PairwiseTableEntity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
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
	
	private void onExportButtonClicked() {
		
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
