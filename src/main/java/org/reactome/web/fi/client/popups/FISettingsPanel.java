package org.reactome.web.fi.client.popups;


import org.reactome.web.fi.common.CommonButton;
import org.reactome.web.fi.common.IDGIconButton;
import org.reactome.web.fi.model.FILayoutType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

/**
 * 
 * @author brunsont
 *
 */
public class FISettingsPanel extends DialogBox implements ChangeHandler {
	
	public interface LayoutChangeHandler{
		void onLayoutChange(FILayoutType type);
		void showDrugs();
		void hideDrugs();
	}

	private LayoutChangeHandler handler;
	
	private ListBox layoutSelector;
	private String currentLayout;
	private CommonButton overlayDrugs;
	private CommonButton rerunCoseLayout;
	private boolean showingDrugs = false;
	
	public FISettingsPanel(String currentLayout, LayoutChangeHandler handler) {
		this.currentLayout = currentLayout;
		this.handler = handler;
		setAutoHideEnabled(true);
		setModal(false);
		this.setStyleName(FICONTEXTRESOURCES.getCSS().fipopup());
		FlowPanel main = new FlowPanel();
		
		layoutSelector = new ListBox();
		
		main.add(getTitlePanel());
		main.add(getLayoutSelector());		
		main.add(this.getrerunCoseLayout());
		main.add(getShowDrugsButton());
		
		setSelections();
		initHandlers();
		
		setWidget(main);
		
	}
	
	private FlowPanel getTitlePanel() {
		FlowPanel fp = new FlowPanel();
		fp.setStyleName(FICONTEXTRESOURCES.getCSS().titlePanel());
		IDGIconButton closeButton = new IDGIconButton(FICONTEXTRESOURCES.gear(), FICONTEXTRESOURCES.getCSS().settings(), "Close Options");
		closeButton.addClickHandler(e -> closeButtonClicked());
		fp.add(closeButton);
		InlineLabel title = new InlineLabel("FIView Options");
		title.setStyleName(FICONTEXTRESOURCES.getCSS().titleLabel());
		fp.add(title);
		return fp;
	}
	
	public void closeButtonClicked() {
		super.hide();
		
	}

	private FlowPanel getLayoutSelector() {
		FlowPanel result = new FlowPanel();
		result.setStyleName(FICONTEXTRESOURCES.getCSS().layoutPanel());
		
		Label title = new Label("Choose Layout:");
		title.setStyleName(FICONTEXTRESOURCES.getCSS().layoutLabel());
		result.add(title);
		
		result.add(layoutSelector = new ListBox());
		layoutSelector.setMultipleSelect(false);
		
		return result;
	}

	private FlowPanel getShowDrugsButton() {
		FlowPanel result = new FlowPanel();
		result.add(overlayDrugs = new CommonButton("Show Drugs", FICONTEXTRESOURCES.getCSS().button(), e -> onShowDrugTargetsClicked()));
		
		return result;
	}
	
	private CommonButton getrerunCoseLayout() {
		rerunCoseLayout = new CommonButton("re-run Layout", FICONTEXTRESOURCES.getCSS().button(), e -> onrerunCoseLayout());
		return rerunCoseLayout;
	}

	private void onShowDrugTargetsClicked() {
		if(showingDrugs)handler.hideDrugs();
		else handler.showDrugs();
		
		showingDrugs = !showingDrugs;
		updateButtonText();
	}

	private void onrerunCoseLayout(){
    	handler.onLayoutChange(FILayoutType.getType(currentLayout));
	}

	private void updateButtonText() {
		if(showingDrugs) overlayDrugs.setText("Remove Drugs");
		else overlayDrugs.setText("Show Drugs");
	}

	private void setSelections() {
		for(FILayoutType type : FILayoutType.values()) 
			layoutSelector.addItem(type.getName());
		for(int i=0; i<layoutSelector.getItemCount(); i++) {
			if(FILayoutType.getType(layoutSelector.getItemText(i)).toString().toLowerCase() == currentLayout)
				layoutSelector.setSelectedIndex(i);
		}
	}
	
	public void resetShowingDrugs() {
		this.showingDrugs = false;
		updateButtonText();
	}

	@Override
	public void onChange(ChangeEvent event) {
		if(event.getSource()!=layoutSelector) return;
		ListBox lb = (ListBox) event.getSource();
        String aux = lb.getSelectedValue();
        this.currentLayout = aux;
    	handler.onLayoutChange(FILayoutType.getType(aux));
    	
    	if(FILayoutType.getType(currentLayout) == FILayoutType.COSE)
    		rerunCoseLayout.setVisible(true);
    	else rerunCoseLayout.setVisible(false);
	}
	
	private void initHandlers() {
		layoutSelector.addChangeHandler(this);
	}
	
	public static FIContextResources FICONTEXTRESOURCES;
	static {
		FICONTEXTRESOURCES = GWT.create(FIContextResources.class);
		FICONTEXTRESOURCES.getCSS().ensureInjected();
	}
	
	public interface FIContextResources extends ClientBundle {
		@Source(ResourceCSS.CSS)
		ResourceCSS getCSS();
		
		@Source("../images/gear.png")
		ImageResource gear();
	}
	
	@CssResource.ImportedWithPrefix("idgDiagram-FISettingsPanel")
	public interface ResourceCSS extends CssResource {
		String CSS = "org/reactome/web/fi/client/FISettingsPanel.css";
		
		String fipopup();
		
		String titlePanel();
		
		String titleLabel();
		
		String layoutLabel();
		
		String button();
								
		String layoutPanel();
		
		String settings();
	}
}
