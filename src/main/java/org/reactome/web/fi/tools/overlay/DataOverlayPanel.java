package org.reactome.web.fi.tools.overlay;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import org.reactome.web.fi.data.loader.TCRDInfoLoader;
import org.reactome.web.fi.data.overlay.model.ExpressionTypeEntities;
import org.reactome.web.fi.data.overlay.model.ExpressionTypeEntity;
import org.reactome.web.fi.events.MakeOverlayRequestEvent;
import org.reactome.web.fi.model.OverlayDataType;
import org.reactome.web.fi.common.MultiSelectListBox;

/**
 * 
 * @author brunsont
 *
 */
public class DataOverlayPanel  extends FlowPanel implements ClickHandler, ChangeHandler{

	private EventBus eventBus;
	private Map<Integer, String> selectorMap;
	private ExpressionTypeEntities expressionTypes;
	private FlowPanel selectionPanel;
	private ListBox eTypeSelector;
	private MultiSelectListBox tissueSelector;
	private Button eTypeButton;
	private Button tissuesSelectedButton;
	private int currentEType;
	
	public DataOverlayPanel(EventBus eventBus) {
		this.eventBus = eventBus;
		this.getElement().getStyle().setMargin(5, Unit.PX);
		
		SimplePanel title = new SimplePanel();
		title.add(new InlineLabel("Overlay Data"));
		title.addStyleName(RESOURCES.getCSS().title());
		this.add(title);
		
		SimplePanel explanation = new SimplePanel();
		explanation.getElement().setInnerHTML(RESOURCES.tissueExpressionInfo().getText());
		explanation.setStyleName(RESOURCES.getCSS().expressionText());
		this.add(explanation);
		
		selectionPanel = new FlowPanel();
		selectionPanel.addStyleName(RESOURCES.getCSS().expressionSubmission());
		selectionPanel.addStyleName(RESOURCES.getCSS().expressionMainSubmitter());
		selectionPanel.add(new InlineLabel("Select expression type:"));
		selectionPanel.add(eTypeSelector = new ListBox());
		eTypeSelector.setMultipleSelect(false);
		eTypeButton = new Button("Get Tissues", this);
		selectionPanel.add(eTypeButton);
		this.add(selectionPanel);
		
		
		FlowPanel tissueSelectionPanel = new FlowPanel();
		tissueSelectionPanel.addStyleName(RESOURCES.getCSS().tissueSelectorPanel());
		tissueSelectionPanel.add(new Label("Select Tissues (hold Ctrl to select multiple):"));
		tissueSelector = new MultiSelectListBox();
		tissueSelector.addStyleName(RESOURCES.getCSS().tissueSelector());
		tissueSelector.setVisibleItemCount(10);
		tissueSelector.setMultipleSelect(true);
		tissueSelectionPanel.add(tissueSelector);
		FlowPanel bottomContainer = new FlowPanel();
		Label contextLabel = new Label("Select a Maximum of 12 tissues");
		contextLabel.getElement().getStyle().setFloat(Style.Float.LEFT);
		bottomContainer.add(contextLabel);
		bottomContainer.add(tissuesSelectedButton = new Button("Overlay!", this));
		tissueSelectionPanel.add(bottomContainer);
		this.add(tissueSelectionPanel);
		
		getExpressionTypes();
		
	}

	protected void setExpressionTypes() {
		eTypeSelector.addItem("Choose an available expression type...", "-1");
		selectorMap = new HashMap<>();
		List<ExpressionTypeEntity> entityList = expressionTypes.getExpressionTypeEntity();
		for(int i=0; i < entityList.size(); i++) {
			eTypeSelector.addItem(entityList.get(i).getName());
			selectorMap.put(i, entityList.get(i).getName());
		}		
	}
	
	protected void setTissueListBoxTypes(List<String> tissueList) {
		tissueSelector.clear();
		if(tissueList.size() > 0) {
			for(String tissue : tissueList)
				tissueSelector.addItem(tissue);
		} else {
			tissueSelector.addItem("No tissues. Press 'Overlay' to Overlay Data.");
		}
		tissueSelector.addChangeHandler(this);
		onChange(null);
		
	}
	
	@Override
	public void onChange(ChangeEvent event) {
		List<String> selectedTissues = tissueSelector.getSelectedItemsText();
		if(selectedTissues == null || selectedTissues.size()>12) {
			tissuesSelectedButton.setEnabled(false);
			tissuesSelectedButton.setTitle("Please choose between 1 and 12 tissues");
		}
		else {
			tissuesSelectedButton.setEnabled(true);
			tissuesSelectedButton.setTitle("Perform Overlay");

		}
	}
	
	@Override
	public void onClick(ClickEvent event) {
		if(event.getSource() == eTypeButton && eTypeSelector.getSelectedIndex() != 0) {
			getTissueTypes(eTypeSelector.getSelectedItemText());
			currentEType = eTypeSelector.getSelectedIndex();
		}
		else if(event.getSource() == tissuesSelectedButton) {
			if(currentEType == 0)
				return;
			
			String valueType = expressionTypes.getExpressionTypeEntity().get(currentEType-1).getDataType();
			
			if(currentEType == 1) {
				valueType = "String";
				eventBus.fireEventFromSource(new MakeOverlayRequestEvent(OverlayDataType.TISSUE_EXPRESSION, "\n" + selectorMap.get(0), valueType), this);
			}
			else if(currentEType > 1) {
				String expressionPostData = String.join(",",tissueSelector.getSelectedItemsText()) 
						+ "\n" + selectorMap.get(currentEType-1);
				eventBus.fireEventFromSource(new MakeOverlayRequestEvent(OverlayDataType.TISSUE_EXPRESSION, expressionPostData, valueType), this);
			}
		}
	}
	
	private void getExpressionTypes() {
		TCRDInfoLoader.loadExpressionTypes(new TCRDInfoLoader.ETypeHandler() {
			
			@Override
			public void onExpressionTypesLoadedError(Throwable exception) {
				exception.printStackTrace();
			}
			
			@Override
			public void onExpressionTypesLoaded(ExpressionTypeEntities entities) {
				expressionTypes = entities;
				setExpressionTypes();
			}
		});
	}
	
	private void getTissueTypes(String eType) {
		TCRDInfoLoader.loadTissueTypes(eType, new TCRDInfoLoader.TissueHandler() {
			@Override
			public void onTissueTypesLoaded(List<String> tissueList) {
				setTissueListBoxTypes(tissueList);
			}
			@Override
			public void onTissueTypesLoadedError(Throwable exception) {
				exception.printStackTrace();
			}
		});
	}

	public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }
    
    public interface Resources extends ClientBundle{
    	@Source(ResourceCSS.CSS)
    	ResourceCSS getCSS();
    	
    	@Source("TissueExpressionInfo.html")
    	TextResource tissueExpressionInfo();
    }
    
    @CssResource.ImportedWithPrefix("idg-overlayData")
    public interface ResourceCSS extends CssResource{
    	String CSS = "org/reactome/web/fi/tools/overlay/DataOverlayPanel.css";
    	
    	String title();
    	
    	String expressionText();
    	
    	String expressionSubmission();
    	
    	String expressionMainSubmitter();
    	
    	String tissueSelector();
    	
    	String tissueSelectorPanel();
    	
    }

}