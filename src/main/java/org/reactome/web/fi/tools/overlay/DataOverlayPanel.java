package org.reactome.web.fi.tools.overlay;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reactome.web.fi.data.loader.TCRDInfoLoader;
import org.reactome.web.fi.data.overlay.model.ExpressionTypeEntities;
import org.reactome.web.fi.data.overlay.model.ExpressionTypeEntity;
import org.reactome.web.fi.data.overlay.model.OverlayProperties;
import org.reactome.web.fi.events.MakeOverlayRequestEvent;
import org.reactome.web.fi.model.OverlayDataType;
import org.reactome.web.fi.common.IDGTextBox;
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
	private IDGTextBox tissueFilter;
	private Button overlayButton;
	private String currentExpressionType;
	private FlowPanel sexChoice;
	private List<RadioButton> radioButtons = new ArrayList<>();
	
	private Label currentSelectionLabel;
	
	private Image loader;
	
	private OverlayProperties currentProperties;
		
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
		
		//make eType selection above tissue selector
		selectionPanel = new FlowPanel();
		selectionPanel.addStyleName(RESOURCES.getCSS().expressionSubmission());
		selectionPanel.addStyleName(RESOURCES.getCSS().expressionMainSubmitter());
		selectionPanel.add(new InlineLabel("Select expression type:"));
		selectionPanel.add(eTypeSelector = new ListBox());
		eTypeSelector.addChangeHandler(e -> eTypeChangedHandler());
		eTypeSelector.setMultipleSelect(false);
		this.add(selectionPanel);
				
		//make container for left side of outerPanel
		FlowPanel leftContainer = new FlowPanel();
		leftContainer.setStyleName(RESOURCES.getCSS().leftContainerPanel());
		leftContainer.add(new Label("Select Tissues (hold Ctrl to select multiple):"));
		
		//add filter text box
		leftContainer.add(tissueFilter = new IDGTextBox());
		tissueFilter.addValueChangeHandler(e -> onTissueFilterChange(e));
		tissueFilter.addKeyUpHandler(e -> tissueFilterKeyUp(e));
		tissueFilter.getElement().setPropertyString("placeholder", "Type a filter and press Enter");
		tissueFilter.setStyleName(RESOURCES.getCSS().tissueFilter());

		//add tissue selector
		tissueSelector = new MultiSelectListBox();
		tissueSelector.addStyleName(RESOURCES.getCSS().tissueSelector());
		tissueSelector.setVisibleItemCount(9);
		tissueSelector.setMultipleSelect(true);
		leftContainer.add(tissueSelector);
		
		//make right container panel
		FlowPanel rightContainer = new FlowPanel();
		rightContainer.setStyleName(RESOURCES.getCSS().rightContainerPanel());
		
		//getThe sexChoice Panel
		sexChoice = getSexChoicePanel();
		sexChoice.setStyleName(RESOURCES.getCSS().propertiesPanel());
		
		//panel for extra properties
		FlowPanel propertiesPanel = new FlowPanel();
		propertiesPanel.getElement().getStyle().setHeight(105, Unit.PX);
		propertiesPanel.add(sexChoice);
		rightContainer.add(propertiesPanel);
		
		//panel for visualising selected tissues
		rightContainer.add(getCurrentSelectionPanel());
		
		//adding right panel to options panel
		FlowPanel optionsPanel = new FlowPanel();
		optionsPanel.add(leftContainer);
		optionsPanel.add(rightContainer);
		optionsPanel.getElement().getStyle().setHeight(200, Unit.PX);
		
		//add options panel to the outerPanel
		FlowPanel outerPanel = new FlowPanel();
		outerPanel.addStyleName(RESOURCES.getCSS().tissueSelectorPanel());
		outerPanel.add(optionsPanel);
		
		//make bottomContainer contining the overlay button and some helper text for the maximum number of tissues selectable at once.
		FlowPanel bottomContainer = new FlowPanel();
		bottomContainer.getElement().getStyle().setMarginTop(35, Unit.PX);
		Label contextLabel = new Label("Select a Maximum of 12 tissues");
		contextLabel.getElement().getStyle().setFloat(Style.Float.LEFT);
		contextLabel.getElement().getStyle().setPaddingLeft(5, Unit.PX);
		bottomContainer.add(contextLabel);
		bottomContainer.add(overlayButton = new Button("Overlay!"));
		overlayButton.addClickHandler(e -> overlayButtonClicked());
		
		//adds loading icon to bottomContainer when server call is being made
		bottomContainer.add(loader = new Image(RESOURCES.loader()));
		loader.setStyleName(RESOURCES.getCSS().tissuesLoading());
		loader.setVisible(false);
		
		bottomContainer.getElement().getStyle().setDisplay(Display.BLOCK);
		outerPanel.add(bottomContainer);
		outerPanel.getElement().getStyle().setHeight(230, Unit.PX);
		this.add(outerPanel);
		
		loadExpressionTypes();
	}

	/**
	 * Panel to display currently selected tissues/cell lines
	 * @return
	 */
	private FlowPanel getCurrentSelectionPanel() {
		FlowPanel result = new FlowPanel();
		
		ScrollPanel sp = new ScrollPanel();
		sp.setStyleName(RESOURCES.getCSS().currentSelectionScrollPanel());
		sp.setAlwaysShowScrollBars(true);
		
		sp.add(currentSelectionLabel = new Label());
		
		result.add(new Label("Currently Selected Types:"));
		result.add(sp);
		return result;
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
			tissueSelector.setListItems(tissueList);
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
			overlayButton.setEnabled(false);
			overlayButton.setTitle("Please choose between 1 and 12 tissues");
		}
		else {
			overlayButton.setEnabled(true);
			overlayButton.setTitle("Perform Overlay");
		}
		Collections.sort(selectedTissues);
		String currentSelection = String.join(", ", selectedTissues);
		currentSelectionLabel.setText(currentSelection);
	}
	/**
	 * causes onbrowserEvent to be fired on key up from textbox
	 * @param e
	 */
	private void tissueFilterKeyUp(KeyUpEvent e) {
		GWT.log(tissueFilter.getText());
	}

	/**
	 * gets textbox value on every key up
	 * @param e
	 */
	private void onTissueFilterChange(ValueChangeEvent<String> e) {
		tissueSelector.filter(e.getValue());
	}

	/**
	 * Makes overlay request based on selected eType, the value type of 
	 * that eType, a unit, and the list of selected tissues.
	 */
	private void overlayButtonClicked() {
		//gets return value type to be used in data mediation after server call
		String valueType = selectorMap.get(currentExpressionType).getDataType();
		String unit = selectorMap.get(currentExpressionType).getUnit();
		String sex = null;
		if(selectorMap.get(currentExpressionType).getHasGender())
			for(RadioButton btn : radioButtons)
				if(btn.getValue())
					sex = btn.getText();

		OverlayProperties properties = new OverlayProperties(valueType, unit, sex, 
															 String.join(",", tissueSelector.getSelectedItemsText()), 
															 currentExpressionType);
		
		if(currentProperties != null && currentProperties.equals(properties))
			return;
		
		currentProperties = properties;
		loader.setVisible(true);
		eventBus.fireEventFromSource(new MakeOverlayRequestEvent(OverlayDataType.TISSUE_EXPRESSION, properties), this);
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
    	
    	String rightContainerPanel();
    	
    	String leftContainerPanel();
    	
    	String propertiesPanel();
    	
    	String currentSelectionScrollPanel();
    	
    	String tissueFilter();
    	
    }

}