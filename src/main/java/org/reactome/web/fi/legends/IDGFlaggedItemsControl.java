package org.reactome.web.fi.legends;

import java.util.List;
import java.util.Set;

import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.events.DiagramObjectsFlaggedEvent;
import org.reactome.web.diagram.legends.FlaggedItemsControl;
import org.reactome.web.fi.events.FIDiagramObjectsFlaggedEvent;

import com.google.gwt.event.shared.EventBus;

public class IDGFlaggedItemsControl  extends FlaggedItemsControl{

	public IDGFlaggedItemsControl(EventBus eventBus) {
		super(eventBus);
	}

	@Override
	public void onDiagramObjectsFlagged(DiagramObjectsFlaggedEvent event) {
		super.term = event.getTerm();
		if(super.term.contains(","))
			term = term.substring(0, term.indexOf(","));
		
		super.includeInteractors = event.getIncludeInteractors();
		
		String msg;
		
		if(event instanceof FIDiagramObjectsFlaggedEvent) {
			List<String> proteinsToFlag = ((FIDiagramObjectsFlaggedEvent)event).getProteinsToFlag();
			int num = proteinsToFlag != null ? proteinsToFlag.size() : 1;
			msg = " - " + num + (num == 1 ? " protein" : " proteins") + " flagged";
		}
		else {
			//There is a case where the DiagramObjectsFlaggedEvent gets fired with a null value for getFlaggedItems();
			//Happens when DiagramViewerImpl runs flaggedElementsLoaded with falsey includeInteractors while the view is FIViewVisualizer
			Set<DiagramObject> flaggedItems =  event.getFlaggedItems();
			int num = flaggedItems != null ? flaggedItems.size() : 1;
	        msg = " - " + num + (num == 1 ? " entity" : " entities") + " flagged";
		}
		
		super.msgLabel.setText(term + msg);
		super.loadingIcon.setVisible(false);
		super.interactorsLabel.setVisible(false);
		super.selector.setVisible(false);
		setVisible(true);
	}
	
	
	
}
