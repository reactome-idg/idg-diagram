package org.reactome.web.fi.events;

import java.util.HashSet;

import org.reactome.web.diagram.events.DiagramObjectsFlaggedEvent;

public class FIDiagramObjectsFlaggedEvent extends DiagramObjectsFlaggedEvent{

	private int entityNumber;
	
	public FIDiagramObjectsFlaggedEvent(String term, Boolean includeInteractors, int entityNumber, boolean notify) {
		super(term, includeInteractors, new HashSet<>(), notify);
		this.entityNumber = entityNumber;
	}

	public int getEntityNumber() {
		return entityNumber;
	}

	public void setEntityNumber(int entityNumber) {
		this.entityNumber = entityNumber;
	}
	
}
