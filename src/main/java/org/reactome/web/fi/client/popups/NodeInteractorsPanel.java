package org.reactome.web.fi.client.popups;

import java.util.List;

import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseNumberEntity;
import org.reactome.web.fi.tools.overlay.pairwise.factory.PairwiseOverlayFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * 
 * @author brunsont
 *
 */
public class NodeInteractorsPanel extends Composite{

	private String id;
	private	FlexTable table;
	
	public NodeInteractorsPanel(String id, String name) {
		this.id = id;
				
		FlowPanel outerPanel = new FlowPanel();
		outerPanel.add(getTableHeader());
		
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.setStyleName(RESOURCES.getCSS().scrollPanel());
		scrollPanel.add(table = new FlexTable());
		outerPanel.add(scrollPanel);
		
		buildTable();
		initWidget(outerPanel);		
	}

	/**
	 * Make separate header flex box b/c FlexBox does not support adding headers
	 * @return
	 */
	private FlexTable getTableHeader() {
		FlexTable header = new FlexTable();
		header.setStyleName(RESOURCES.getCSS().headerTable());
		header.setText(0, 0, "Source Description");
		header.setText(0, 1, "num");
		header.getFlexCellFormatter().setColSpan(0,0,2);
		return header;
	}
	
	private void buildTable() {
		table.clear();
		table.setStyleName(RESOURCES.getCSS().dataTable());
		
		if(PairwiseOverlayFactory.get().getCurrentPairwiseProperties() == null 
				|| PairwiseOverlayFactory.get().getCurrentPairwiseProperties().size() == 0
				|| PairwiseOverlayFactory.get().getPairwiseNumberEntities() == null) {
			table.setText(0, 0, "No relationshps for any data set");
			return;
		}
		
		List<PairwiseNumberEntity> entities = PairwiseOverlayFactory.get().getPairwiseNumberEntities();
		
		int counter = 0;
		
		for(int i=0; i<entities.size(); i++) {
			PairwiseNumberEntity entity = entities.get(i);
			if(entity.getGene() != id) continue; //continue if not an entity we want
			
			table.setText(counter, 0, entity.getDataDesc().getId());
			table.setText(counter, 1, (entity.getPosNum()+entity.getNegNum())+"");
			table.getFlexCellFormatter().setColSpan(counter, 0, 3);
			
			//if row is even, change background color to make it easier to read
			if(counter % 2 == 0) {
				table.getRowFormatter().getElement(counter).getStyle().setBackgroundColor("#066b9e");
			}
			counter++;
		}
		
		if(counter == 0) {
			table.setText(0, 0, "No relationshps for any data set");
		}
	}
	
	public void updateWidget() {
		buildTable();
	}
	
	public static Resources RESOURCES;
	static {
		RESOURCES = GWT.create(Resources.class);
		RESOURCES.getCSS().ensureInjected();
	}
	
	public interface Resources extends ClientBundle {
		@Source(ResourceCSS.CSS)
		ResourceCSS getCSS();
	}
	
	@CssResource.ImportedWithPrefix("idg-NodeOverlayPanel")
	public interface ResourceCSS extends CssResource {
		String CSS = "org/reactome/web/fi/client/popups/NodeOverlayPanel.css";
		
		String headerTable();
		
		String dataTable();
		
		String scrollPanel();
	}
}
