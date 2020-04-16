package org.reactome.web.fi.tools.popup.tables;

import java.util.List;

import org.reactome.web.fi.tools.popup.tables.columns.ActionTypeColumn;
import org.reactome.web.fi.tools.popup.tables.columns.ActivityTypeColumn;
import org.reactome.web.fi.tools.popup.tables.columns.ActivityValueColumn;
import org.reactome.web.fi.tools.popup.tables.columns.DrugNameColumn;
import org.reactome.web.fi.tools.popup.tables.columns.TargetGeneColumn;
import org.reactome.web.fi.tools.popup.tables.models.DrugTargetResult;

import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;

import com.google.gwt.view.client.ListDataProvider;

/**
 * 
 * @author brunsont
 *
 */
public class DrugTargetResultsTable extends DataGrid<DrugTargetResult> {

	public final static Integer PAGE_SIZE = 10;
	
	
	public DrugTargetResultsTable(List<DrugTargetResult> entities, ListDataProvider<DrugTargetResult> provider, SimplePager pager) {
		super(PAGE_SIZE);
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
		
		pager.setDisplay(this);
		pager.setPageSize(PAGE_SIZE);
		
		this.setRowCount(entities.size(), true);
		this.redraw();
	}
	
}
