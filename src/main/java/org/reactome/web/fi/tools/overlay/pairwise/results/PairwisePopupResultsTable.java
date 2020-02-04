package org.reactome.web.fi.tools.overlay.pairwise.results;

import java.util.Comparator;
import java.util.List;

import org.reactome.web.fi.common.IDGPager;
import org.reactome.web.fi.tools.overlay.pairwise.PairwiseTableEntity;
import org.reactome.web.fi.tools.overlay.pairwise.results.columns.DiagramGeneNameColumn;
import org.reactome.web.fi.tools.overlay.pairwise.results.columns.OverlayValueColumn;
import org.reactome.web.fi.tools.overlay.pairwise.results.columns.PairwiseInteractorColumn;
import org.reactome.web.fi.tools.overlay.pairwise.results.columns.PairwiseRelationshipColumn;
import org.reactome.web.fi.tools.overlay.pairwise.results.columns.PairwiseSourceColumn;

import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.Handler;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.view.client.ListDataProvider;

/**
 * 
 * @author brunsont
 *
 */
public class PairwisePopupResultsTable extends DataGrid<PairwiseTableEntity>{
	public final static Integer PAGE_SIZE = 10;
	
	
	public PairwisePopupResultsTable(List<PairwiseTableEntity> entities, ListDataProvider<PairwiseTableEntity> provider, IDGPager pager) {
		super(PAGE_SIZE);
		this.setRowData(0, entities);
		this.setAutoHeaderRefreshDisabled(true);
		this.setWidth("100%");
		this.setVisible(true);
		this.setHeight("200px");
		
		PairwiseInteractorColumn pairwiseColumn;
		this.addColumn(pairwiseColumn = new PairwiseInteractorColumn(), "Interactor");
//		pairwiseColumn.setSortable(true);
		this.addColumn(new DiagramGeneNameColumn(), "Source");
		this.addColumn(new OverlayValueColumn(), "Overlay Value");
		this.addColumn(new PairwiseSourceColumn(), "Interaction Source");
		this.addColumn(new PairwiseRelationshipColumn(), "Pos/Neg");
				
		//ListDataProvider setup
		provider.addDataDisplay(this);
		provider.setList(entities);
		
		//Pager setup
		pager.setDisplay(this);
		pager.setPageSize(PAGE_SIZE);
		
		this.setRowCount(entities.size(), true);
		
		//ListHandler for sorting
		ListHandler<PairwiseTableEntity> columnSortHandler = new ListHandler<>(provider.getList());
		columnSortHandler.setComparator(pairwiseColumn, new Comparator<PairwiseTableEntity>() {
			@Override
			public int compare(PairwiseTableEntity o1, PairwiseTableEntity o2) {
	          return o1.getInteractorName().compareTo(o2.getInteractorName());
	        }
			
		});
		
		this.addColumnSortHandler(columnSortHandler);
		this.getColumnSortList().push(pairwiseColumn);
		
		this.redraw();
	}
}
