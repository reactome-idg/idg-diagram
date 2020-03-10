package org.reactome.web.fi.client.popups;

import java.util.List;
import java.util.Map;

import org.reactome.web.diagram.profiles.analysis.AnalysisColours;
import org.reactome.web.fi.model.DataOverlay;
import org.reactome.web.fi.model.DataOverlayEntity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.ScrollPanel;

public class NodeOverlayPanel extends Composite{

	private EventBus eventBus;
	private String id;
	private DataOverlay overlay;
	private FlexTable table;
	
	public NodeOverlayPanel(EventBus eventBus, String id, DataOverlay overlay) {
		this.eventBus = eventBus;
		this.id = id;
		this.overlay = overlay;
		
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
		FlexTable result = new FlexTable();
		result.setStyleName(RESOURCES.getCSS().headerTable());
		result.setText(0, 0, "Tissue/Cell Line");
		result.setText(0, 1, "Expression");
		result.getFlexCellFormatter().setColSpan(0, 0, 3);	
		result.getFlexCellFormatter().setAlignment(0, 1, HasAlignment.ALIGN_CENTER, HasAlignment.ALIGN_BOTTOM);;
		return result;
	}

	private void buildTable() {
		table.clear();
		table.setStyleName(RESOURCES.getCSS().dataTable());

		List<DataOverlayEntity> entities = overlay.getUniprotToEntitiesMap().get(id);
		
		//handle case where no expression exists in any column
		if(entities == null || entities.size() == 0) {
			table.setText(0, 0, "N/A for all tissues or cell lines.");
		}
		
		//add row for each tissue. Col 0 is the tissue/cell line name.
		//Col 1 is the formatted expression value
		for(int i=0; i<entities.size(); i++) {
			table.setText(i, 0, entities.get(i).getTissue());
			table.setText(i, 1, getFormatedValue(entities.get(i).getValue()));
			table.getFlexCellFormatter().setColSpan(i, 0, 3);
			table.getRowFormatter().getElement(i).getStyle().setBackgroundColor(
					AnalysisColours.get().expressionGradient
					.getColor(entities.get(i).getValue(), overlay.getMinValue(), overlay.getMaxValue()));
		}
	}

	private String getFormatedValue(Double value) {
		return NumberFormat.getFormat("#.##E0").format(value);
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
