package org.reactome.web.fi.events;

import java.util.HashSet;
import java.util.List;

import org.reactome.web.diagram.events.DiagramObjectsFlaggedEvent;

public class FIDiagramObjectsFlaggedEvent extends DiagramObjectsFlaggedEvent{

	private List<String> proteinsToFlag;
	
	public FIDiagramObjectsFlaggedEvent(String term, Boolean includeInteractors, List<String> proteinsToFlag, boolean notify) {
		super(term, includeInteractors, new HashSet<>(), notify);
		this.proteinsToFlag = proteinsToFlag;
	}

	public List<String> getProteinsToFlag() {
		return proteinsToFlag;
	}

	public void setProteinsToFlag(List<String> proteinsToFlag) {
		this.proteinsToFlag = proteinsToFlag;
	}

}
