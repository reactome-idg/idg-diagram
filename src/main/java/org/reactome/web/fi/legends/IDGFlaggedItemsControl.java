package org.reactome.web.fi.legends;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.events.DiagramObjectsFlaggedEvent;
import org.reactome.web.diagram.legends.FlaggedItemsControl;
import org.reactome.web.fi.data.manager.StateTokenHelper;
import org.reactome.web.fi.events.FIDiagramObjectsFlaggedEvent;
import org.reactome.web.fi.events.SetFIFlagDataDescsEvent;
import org.reactome.web.fi.handlers.SetFIFlagDataDescsHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * 
 * @author brunsont
 *
 */
public class IDGFlaggedItemsControl  extends FlaggedItemsControl implements SetFIFlagDataDescsHandler{
	
	private List<String> dataDescs;
	private FlowPanel prdPanel;
	private TextBox prdInput;
	
	private FlowPanel fdrPanel;
	private TextBox fdrInput;
	
	public IDGFlaggedItemsControl(EventBus eventBus) {
		super(eventBus);
		
		super.selector.removeFromParent();		
		this.getElement().getStyle().setHeight(56, Unit.PX);
		
		//Remove default style and make the msgLabel wider
		//Required because the default width is tagged with !important
		this.msgLabel.removeStyleName(msgLabel.getStyleName());
		this.msgLabel.addStyleName(IDGRESOURCES.getCSS().idgFlaggedItemsLabel());
		
		FlowPanel panel = new FlowPanel();
		panel.getElement().getStyle().setFloat(Float.LEFT);
		
		prdInput = new TextBox();
		
		//setting attributes on TextBox to make it function better
		prdInput.getElement().setAttribute("type", "number");
		prdInput.getElement().setAttribute("min", "0");
		prdInput.getElement().setAttribute("max", "1");
		
		prdInput.setStyleName(IDGRESOURCES.getCSS().prdInput());
		prdInput.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					updatePRD();
				}
			}
		});
		
		prdInput.setValue("0.9"); //set defaul PRD value to 0.9
		InlineLabel prdLbl = new InlineLabel("FI Score ≥");
		prdLbl.getElement().getStyle().setFloat(Float.NONE);
		prdPanel =new FlowPanel();
		prdPanel.getElement().getStyle().setFloat(Float.LEFT);
		prdPanel.add(prdLbl);
		prdPanel.add(prdInput);
		panel.add(prdPanel);
		prdPanel.setVisible(true);
		
		fdrInput= new TextBox();
		
		//seting attributes on TexBox to make it function better
		fdrInput.getElement().setAttribute("type", "number");
		fdrInput.getElement().setAttribute("min", "0");
		fdrInput.getElement().setAttribute("max", "1");
		fdrInput.setStyleName(IDGRESOURCES.getCSS().prdInput());
		fdrInput.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					updateFDR();
				}
			}
		});
		
		fdrInput.setValue("0.05");
		InlineLabel fdrLbl = new InlineLabel("fdr ≤");
		fdrLbl.getElement().getStyle().setFloat(Float.NONE);
		fdrPanel = new FlowPanel();
		fdrPanel.getElement().getStyle().setFloat(Float.LEFT);
		fdrPanel.add(fdrLbl);
		fdrPanel.add(fdrInput);
		panel.add(fdrPanel);
		fdrPanel.setVisible(false);
		
		super.add(panel);
		
		eventBus.addHandler(SetFIFlagDataDescsEvent.TYPE, this);
	}

	/**
	 * update SIGCUTOFF token to new value and set new history token to cause update
	 * 
	 */
	private void updatePRD() {
		StateTokenHelper helper = new StateTokenHelper();
		Map<String, String> tokenMap = helper.buildTokenMap(History.getToken());
		tokenMap.put("SIGCUTOFF", prdInput.getValue()+"");
		History.newItem(helper.buildToken(tokenMap));
	}
	
	/**
	 * Update FLGFDR token and set new history token
	 */
	private void updateFDR() {
		StateTokenHelper helper = new StateTokenHelper();
		Map<String, String> tokenMap = helper.buildTokenMap(History.getToken());
		tokenMap.put("FLGFDR", fdrInput.getValue()+"");
		History.newItem(helper.buildToken(tokenMap));
	}

	@Override
	public void onDiagramObjectsFlagged(DiagramObjectsFlaggedEvent event) {
		super.term = event.getTerm();
		
		super.includeInteractors = event.getIncludeInteractors();
		
		String msg;
		
		if(event instanceof FIDiagramObjectsFlaggedEvent) {
			List<String> proteinsToFlag = ((FIDiagramObjectsFlaggedEvent)event).getProteinsToFlag();
			int num = proteinsToFlag != null ? proteinsToFlag.size() : 0;
			msg = " - " + num + (event.getIncludeInteractors() == true ? " interacting " : "") + (num == 1 ? " protein" : " proteins") + " flagged";
		}
		else {
			//There is a case where the DiagramObjectsFlaggedEvent gets fired with a null value for getFlaggedItems();
			//Happens when DiagramViewerImpl runs flaggedElementsLoaded with falsey includeInteractors while the view is FIViewVisualizer
			Set<DiagramObject> flaggedItems =  event.getFlaggedItems();
			int num = flaggedItems != null ? flaggedItems.size() : 1;
	        msg = " - " + num + (event.getIncludeInteractors() == true ? " interacting " : "") + (num == 1 ? " entity" : " entities") + " flagged";
		}
		
		super.msgLabel.setText(term + msg);
		
		if(dataDescs != null  && dataDescs.size() > 0) {
			msgLabel.setTitle(String.join("\n", dataDescs));
		}
		
		super.loadingIcon.setVisible(false);
		super.interactorsLabel.setVisible(false);
		setVisible(true);
	}

	@Override
	public void onSetFIFlagDataDescs(SetFIFlagDataDescsEvent event) {
		this.dataDescs = event.getDataDescs();
		if(dataDescs.contains("combined_score")) {
			prdPanel.setVisible(true);
			StateTokenHelper helper = new StateTokenHelper();
			prdInput.setValue(helper.buildTokenMap(History.getToken()).get("SIGCUTOFF"));
		}
		else prdPanel.setVisible(false);
		
		if(event.containsEncapsulatedPathways()) fdrPanel.setVisible(true);
		else fdrPanel.setVisible(false);
	}
	
	
	
	public static IDGResources IDGRESOURCES;
	static {
		IDGRESOURCES = GWT.create(IDGResources.class);
		IDGRESOURCES.getCSS().ensureInjected();
	}
	
	public interface IDGResources extends ClientBundle {
		@Source (IDGResourceCSS.CSS)
		IDGResourceCSS getCSS();
	}
	
	@CssResource.ImportedWithPrefix("idgDiagram-FlaggedInteractorControl")
	public interface IDGResourceCSS extends CssResource {
		String CSS = "org/reactome/web/fi/legends/IDGFlaggedItemsControl.css";
		
		String idgFlaggedItemsLabel();
		
		String prdInput();
	}
}
