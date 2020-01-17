package org.reactome.web.fi.tools.overlay.pairwise;

import org.reactome.web.fi.client.visualisers.fiview.CytoscapeEntity;

import com.google.gwt.user.client.ui.DialogBox;

public class AbstractPairwisePopup extends DialogBox implements CytoscapeEntity.Handler{

	@Override
	public void onNodeClicked(String id, String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEdgeClicked(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNodeHovered(String id, String name, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEdgeHovered(String id, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNodeMouseOut() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEdgeMouseOut() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCytoscapeCoreContextEvent(int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCytoscapeCoreSelectedEvent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEdgeContextSelectEvent(String id, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNodeContextSelectEvent(String id, String name, int x, int y) {
		// TODO Auto-generated method stub
		
	}

}
