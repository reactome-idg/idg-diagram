package org.reactome.web.fi.tools.popup.tables;

import org.reactome.web.fi.tools.overlay.pairwise.model.PairwiseTableEntity;
import org.reactome.web.fi.tools.popup.tables.models.DrugTargetResult;

import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.view.client.ListDataProvider;

public class DrugTargetResultsTable extends DataGrid<DrugTargetResult> {

	public final static Integer PAGE_SIZE = 10;
	
	private ListHandler<PairwiseTableEntity> sorter;
	private ListDataProvider<PairwiseTableEntity> provider;
	
}
