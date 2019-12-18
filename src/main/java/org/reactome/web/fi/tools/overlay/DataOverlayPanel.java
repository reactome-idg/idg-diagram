package org.reactome.web.fi.tools.overlay;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reactome.web.fi.data.loader.TCRDInfoLoader;
import org.reactome.web.fi.data.overlay.model.ExpressionTypeEntities;
import org.reactome.web.fi.data.overlay.model.ExpressionTypeEntity;
import org.reactome.web.fi.data.overlay.model.OverlayProperties;
import org.reactome.web.fi.events.MakeOverlayRequestEvent;
import org.reactome.web.fi.model.OverlayDataType;
import org.reactome.web.fi.common.MultiSelectListBox;

/**
 * 
 * @author brunsont
 *
 */
public class DataOverlayPanel  extends FlowPanel{

	private EventBus eventBus;
	private Map<String, ExpressionTypeEntity> selectorMap;
	private FlowPanel selectionPanel;
	private ListBox eTypeSelector;
	private MultiSelectListBox tissueSelector;
	private Button tissuesSelectedButton;
	private String currentExpressionType;
	private FlowPanel sexChoice;
	private List<RadioButton> radioButtons = new ArrayList<>();
	
	private Image loader;
	
	public DataOverlayPanel(EventBus eventBus) {
		this.eventBus = eventBus;
		this.getElement().getStyle().setMargin(5, Unit.PX);
		
		initPanel();
		
	}

	private void initPanel() {
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
		eTypeSelector.addChangeHandler(e -> eTypeChangedHandler());
		eTypeSelector.setMultipleSelect(false);
		this.add(selectionPanel);
		
		
		FlowPanel outerPanel = new FlowPanel();
		FlowPanel tissueSelectionPanel = new FlowPanel();
		outerPanel.addStyleName(RESOURCES.getCSS().tissueSelectorPanel());
		tissueSelectionPanel.add(new Label("Select Tissues (hold Ctrl to select multiple):"));
		tissueSelector = new MultiSelectListBox();
		tissueSelector.addStyleName(RESOURCES.getCSS().tissueSelector());
		tissueSelector.setVisibleItemCount(10);
		tissueSelector.setMultipleSelect(true);
		tissueSelectionPanel.add(tissueSelector);
		
		sexChoice = getSexChoicePanel();
		sexChoice.setStyleName(RESOURCES.getCSS().sexChoicePanel());
		tissueSelectionPanel.add(sexChoice);
		
		tissueSelectionPanel.getElement().getStyle().setHeight(210, Unit.PX);
		
		outerPanel.add(tissueSelectionPanel);
		
		FlowPanel bottomContainer = new FlowPanel();
		Label contextLabel = new Label("Select a Maximum of 12 tissues");
		contextLabel.getElement().getStyle().setFloat(Style.Float.LEFT);
		bottomContainer.add(contextLabel);
		bottomContainer.add(tissuesSelectedButton = new Button("Overlay!"));
		tissuesSelectedButton.addClickHandler(e -> overlayButtonClicked());
		
		bottomContainer.add(loader = new Image(RESOURCES.loader()));
		loader.setStyleName(RESOURCES.getCSS().tissuesLoading());
		loader.setVisible(false);
		
		bottomContainer.getElement().getStyle().setDisplay(Display.BLOCK);
		outerPanel.add(bottomContainer);
		this.add(outerPanel);
		
		loadExpressionTypes();
	}

	/**
	 * Sets list box of ETypes used to direct tissue selection
	 * @param entities 
	 */
	protected void setExpressionTypes(ExpressionTypeEntities entities) {
		selectorMap = new HashMap<>();
		List<ExpressionTypeEntity> entityList = entities.getExpressionTypeEntity();
		for(int i=0; i < entityList.size(); i++) {
			eTypeSelector.addItem(entityList.get(i).getName());
			selectorMap.put(entityList.get(i).getName(), entityList.get(i));
		}		
	}
	
	/**
	 * Sets list box of tissues. 12 can be selected at one time.
	 * @param tissueList
	 */
	protected void setTissueListBoxTypes(List<String> tissueList) {
		tissueSelector.clear();
		if(tissueList.size() > 0) {
			for(String tissue : tissueList)
				tissueSelector.addItem(tissue);
		} else {
			tissueSelector.addItem("No tissues. Press 'Overlay' to Overlay Data.");
		}
		tissueSelector.addChangeHandler(e -> onListBoxChanged());
		
		sexChoice.setVisible(selectorMap.get(currentExpressionType).getHasGender()); //shows or hides based on EType
		if(!sexChoice.isVisible())
			for(RadioButton btn : radioButtons)
				btn.setValue(false);
	}
	
	/**
	 * Panel containing radioButtons for choice of sex
	 * @return
	 */
	private FlowPanel getSexChoicePanel() {
		FlowPanel fp = new FlowPanel();
		Label lb = new Label("Choose a sex:");
		RadioButton rb1 = new RadioButton("sex", "Male");
		RadioButton rb2 = new RadioButton("sex", "Female");
		radioButtons.add(rb1);
		radioButtons.add(rb2);
		fp.add(lb);
		fp.add(rb1);
		fp.add(rb2);
		return fp;
	}

	/**
	 * Updates tissue selection box based on changes to number of tissues selected
	 */
	private void onListBoxChanged() {
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

	/**
	 * Makes overlay request based on selected eType, the value type of 
	 * that eType, a unit, and the list of selected tissues.
	 */
	private void overlayButtonClicked() {
		loader.setVisible(true);
		//gets return value type to be used in data mediation after server call
		String valueType = selectorMap.get(currentExpressionType).getDataType();
		String unit = selectorMap.get(currentExpressionType).getUnit();
		String sex = null;
		if(selectorMap.get(currentExpressionType).getHasGender())
			for(RadioButton btn : radioButtons)
				if(btn.getValue())
					sex = btn.getText();
		
		OverlayProperties properties = new OverlayProperties(valueType, unit, sex);
		
		String expressionPostData = "";
		if(currentExpressionType == "Target Development Level") {
			expressionPostData = "\n" + currentExpressionType;
		}
		else{
			expressionPostData = String.join(",",tissueSelector.getSelectedItemsText()) 
					+ "\n" + currentExpressionType;
		}
		eventBus.fireEventFromSource(new MakeOverlayRequestEvent(OverlayDataType.TISSUE_EXPRESSION, expressionPostData, properties), this);
	}
	
	public void hideLoader() {
		loader.setVisible(false);
	}
	
	/**
	 * Gets list of tissues for eType when eType is changed
	 */
	private void eTypeChangedHandler() {
		getTissueTypes(eTypeSelector.getSelectedItemText());
		currentExpressionType = eTypeSelector.getSelectedItemText();	
	}
	
	/**
	 * Loads expression types for list box
	 */
	private void loadExpressionTypes() {
		TCRDInfoLoader.loadExpressionTypes(new TCRDInfoLoader.ETypeHandler() {
			
			@Override
			public void onExpressionTypesLoadedError(Throwable exception) {
				exception.printStackTrace();
			}
			
			@Override
			public void onExpressionTypesLoaded(ExpressionTypeEntities entities) {
				setExpressionTypes(entities);
				eTypeChangedHandler();
			}
		});
	}
	
	/**
	 * Loads tissues for a specific EType
	 * @param eType
	 */
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
    	
    	@Source("images/loader.gif")
    	ImageResource loader();
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
    	
    	String tissuesLoading();
    	
    	String sexChoicePanel();
    	
    }

}