package org.reactome.web.fi.tools.overlay;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;

import java.util.ArrayList;
import java.util.List;

import org.reactome.web.fi.data.loader.TCRDInfoLoader;

public class DataOverlay  extends FlowPanel implements ClickHandler{

	private List<String> expressionTypes;
	private FlowPanel selectionPanel;
	private ListBox eTypeSelector;
	
	public DataOverlay() {
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
		this.add(selectionPanel);
		
		getExpressionTypes();
		
	}

	private void getExpressionTypes() {
		TCRDInfoLoader.loadExpressionTypes(new TCRDInfoLoader.Handler() {
			
			@Override
			public void onTCRDInfoError(Throwable exception) {
				expressionTypes = new ArrayList<>();
			}
			
			@Override
			public void onExpressionTypesLoaded(List<String> info) {
				expressionTypes = info;
			}
		});
	}

	@Override
	public void onClick(ClickEvent event) {
		// TODO Auto-generated method stub
		
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
    	String CSS = "org/reactome/web/fi/tools/overlay/DataOverlay.css";
    	
    	String title();
    	
    	String expressionText();
    	
    	String expressionSubmission();
    	
    	String expressionMainSubmitter();
    }
}