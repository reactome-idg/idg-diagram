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
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.TextBox;

/**
 * 
 * @author brunsont
 *
 */
public class IDGFlaggedItemsControl  extends FlaggedItemsControl implements SetFIFlagDataDescsHandler{
	
	private List<String> dataDescs;
	private TextBox prdInput;
	
	
	public IDGFlaggedItemsControl(EventBus eventBus) {
		super(eventBus);
		
		super.selector.removeFromParent();
		
		//Remove default style and make the msgLabel wider
		//Required because the default width is tagged with !important
		this.msgLabel.removeStyleName(msgLabel.getStyleName());
		this.msgLabel.addStyleName(IDGRESOURCES.getCSS().idgFlaggedItemsLabel());
		
		prdInput = new TextBox();
		
		//setting attributes on TextBox to make it function better
		prdInput.getElement().setAttribute("type", "number");
		prdInput.getElement().setAttribute("min", "0");
		prdInput.getElement().setAttribute("max", "1");
		prdInput.getElement().setAttribute("step", "0.1");
		
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
		super.add(prdInput);
		prdInput.setVisible(false);
		
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
		if(dataDescs.contains("Combined Score")) {
			prdInput.setVisible(true);
			StateTokenHelper helper = new StateTokenHelper();
			prdInput.setValue(helper.buildTokenMap(History.getToken()).get("SIGCUTOFF"));
		}
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
