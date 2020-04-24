package org.reactome.web.fi.client.popups;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.reactome.web.fi.common.CommonButton;
import org.reactome.web.fi.common.IDGTextBox;
import org.reactome.web.fi.model.FILayoutType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
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
		void searchProteins(Set<String> searchlist);
	}

	private LayoutChangeHandler handler;
	
	private ListBox layoutSelector;
	private String currentLayout;
	private IDGTextBox proteinSearch;
	private CommonButton overlayDrugs;
	private boolean showingDrugs = false;
	
	public FISettingsPanel(String currentLayout, LayoutChangeHandler handler) {
		this.currentLayout = currentLayout;
		this.handler = handler;
		setAutoHideEnabled(true);
		setModal(false);
		this.setStyleName(FICONTEXTRESOURCES.getCSS().fipopup());
		FlowPanel main = new FlowPanel();
		
		layoutSelector = new ListBox();
		
		main.add(getLayoutSelector());		
		main.add(getShowDrugsButton());
		main.add(getProteinSearchBox());
		
		setSelections();
		initHandlers();
		
		setTitlePanel();
		setWidget(main);
		
	}
	
	private void setTitlePanel() {
		FlowPanel fp = new FlowPanel();
		InlineLabel title = new InlineLabel("FIView Options");
		fp.add(title);
		
		SafeHtml safe = SafeHtmlUtils.fromTrustedString(fp.toString());
		getCaption().setHTML(safe);
		getCaption().asWidget().setStyleName(FICONTEXTRESOURCES.getCSS().header());
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

	private FlowPanel getProteinSearchBox() {
		FlowPanel result = new FlowPanel();
		proteinSearch  = new IDGTextBox();
		proteinSearch.addStyleName(FICONTEXTRESOURCES.getCSS().search());
		proteinSearch.getElement().setPropertyString("placeholder", "O43521,Q9UJU2,JAG1");
		proteinSearch.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
					searchForProteins();
			}
		});
		
		result.add(proteinSearch);
		
		return result;
	}

	protected void searchForProteins() {
		String searchString = proteinSearch.getText();
		Set<String> searchSet = new HashSet<>();
		if(searchString.contains(","))
			searchSet.addAll(Arrays.asList(searchString.split(",")));
		else searchSet.add(searchString);
		
		handler.searchProteins(searchSet);
	}

	private FlowPanel getShowDrugsButton() {
		FlowPanel result = new FlowPanel();
		result.add(overlayDrugs = new CommonButton("Show Drugs", FICONTEXTRESOURCES.getCSS().button(), e -> onShowDrugTargetsClicked()));
		
		return result;
	}

	private void onShowDrugTargetsClicked() {
		if(showingDrugs)handler.hideDrugs();
		else handler.showDrugs();
		
		showingDrugs = !showingDrugs;
		updateButtonText();
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

	@Override
	public void onChange(ChangeEvent event) {
		if(event.getSource()!=layoutSelector) return;
		ListBox lb = (ListBox) event.getSource();
        String aux = lb.getSelectedValue();
    	handler.onLayoutChange(FILayoutType.getType(aux));
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
	}
	
	@CssResource.ImportedWithPrefix("idgDiagram-FISettingsPanel")
	public interface ResourceCSS extends CssResource {
		String CSS = "org/reactome/web/fi/client/FIContextPanel.css";
		
		String fipopup();
		
		String layoutLabel();
		
		String button();
				
		String search();
		
		String header();
		
		String layoutPanel();
	}
}