package org.reactome.web.fi.tools.popup.tables;

import java.util.List;

import org.reactome.web.fi.tools.popup.tables.columns.ActionTypeColumn;
import org.reactome.web.fi.tools.popup.tables.columns.ActivityTypeColumn;
import org.reactome.web.fi.tools.popup.tables.columns.ActivityValueColumn;
import org.reactome.web.fi.tools.popup.tables.columns.DrugNameColumn;
import org.reactome.web.fi.tools.popup.tables.columns.TargetGeneColumn;
import org.reactome.web.fi.tools.popup.tables.models.DrugTargetResult;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * 
 * @author brunsont
 *
 */
public class DrugTargetResultsTable extends DataGrid<DrugTargetResult> {
	
	public interface Handler{
		void onRowClicked(DrugTargetResult entity);
	}

	private Handler handler;
	
	public final static Integer PAGE_SIZE = 10;
	
	private SingleSelectionModel<DrugTargetResult> selectionModel;
	
	
	public DrugTargetResultsTable(List<DrugTargetResult> entities, ListDataProvider<DrugTargetResult> provider, SimplePager pager, Handler handler) {
		super(PAGE_SIZE);
		this.handler = handler;
		this.setRowData(0, entities);
		this.setWidth("100%");
		this.setVisible(true);
		this.setHeight("200px");
		
		provider.addDataDisplay(this);
		provider.setList(entities);
		
		this.addColumn(new DrugNameColumn(), "Drug Name");
		this.addColumn(new TargetGeneColumn(), "Target Protein");
		this.addColumn(new ActionTypeColumn(), "Action Type");
		this.addColumn(new ActivityTypeColumn(), "Activity Type");
		this.addColumn(new ActivityValueColumn(), "Activity Value");
		
		this.addCellPreviewHandler(new CellPreviewEvent.Handler<DrugTargetResult>() {
            @Override
            public void onCellPreview(final CellPreviewEvent<DrugTargetResult> event) {
            	if(event.getNativeEvent().getType().equals("click")) {
            		handler.onRowClicked(event.getValue());
            	}
                if (!event.getNativeEvent().getType().equals("mouseover")) return;
                Element cellElement = event.getNativeEvent().getEventTarget().cast();
                DrugTargetResult model = (DrugTargetResult) DrugTargetResultsTable.this.getValueKey(event.getValue());
                cellElement.setTitle(DrugTargetResultsTable.this.getColumn(event.getColumn()).getValue(model)+"");
            }
        });
		
		pager.setDisplay(this);
		pager.setPageSize(PAGE_SIZE);
		
		selectionModel = new SingleSelectionModel<>();
		this.setSelectionModel(selectionModel);
		
		this.setRowCount(entities.size(), true);
		this.redraw();
	}
	
	public void selectRow(DrugTargetResult entity, boolean select) {
		selectionModel.setSelected(entity, select);
	}

	public void resetSelection() {
		if(selectionModel.getSelectedObject() == null) return;
		selectionModel.setSelected(selectionModel.getSelectedObject(), false);
	}
	
}
