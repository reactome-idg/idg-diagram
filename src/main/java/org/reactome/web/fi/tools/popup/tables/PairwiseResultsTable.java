package org.reactome.web.fi.tools.popup.tables;

import java.util.Comparator;
import java.util.List;

import org.reactome.web.fi.tools.overlay.pairwise.model.PairwiseTableEntity;
import org.reactome.web.fi.tools.overlay.pairwise.tables.columns.DiagramGeneNameColumn;
import org.reactome.web.fi.tools.overlay.pairwise.tables.columns.OverlayValueColumn;
import org.reactome.web.fi.tools.overlay.pairwise.tables.columns.PairwiseInteractorColumn;
import org.reactome.web.fi.tools.overlay.pairwise.tables.columns.PairwiseRelationshipColumn;
import org.reactome.web.fi.tools.overlay.pairwise.tables.columns.PairwiseSourceColumn;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
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
public class PairwiseResultsTable extends DataGrid<PairwiseTableEntity>{
	
	public interface Handler {
		void onRowClicked(PairwiseTableEntity entity);
	}
	
	private Handler handler;
	
	public final static Integer PAGE_SIZE = 10;
	
	private ListHandler<PairwiseTableEntity> sorter;
	private ListDataProvider<PairwiseTableEntity> provider;
	private SingleSelectionModel<PairwiseTableEntity> selectionModel;
	
	
	public PairwiseResultsTable(List<PairwiseTableEntity> entities, ListDataProvider<PairwiseTableEntity> provider, SimplePager pager, Handler handler) {
		
		super(PAGE_SIZE);
		this.handler = handler;
		this.provider = provider;
		this.setRowData(0, entities);
		this.setAutoHeaderRefreshDisabled(true);
		this.setWidth("100%");
		this.setVisible(true);
		this.setHeight("200px");
		
		//ListDataProvider setup
		provider.addDataDisplay(this);
		provider.setList(entities);
		
		sorter = new ListHandler<>(provider.getList());
		
		this.addColumnSortHandler(sorter);
		
		DiagramGeneNameColumn diagramGeneNameColumn = new DiagramGeneNameColumn();
		diagramGeneNameColumn.setSortable(true);
		sorter.setComparator(diagramGeneNameColumn, new Comparator<PairwiseTableEntity>() {
			@Override
			public int compare(PairwiseTableEntity o1, PairwiseTableEntity o2) {
				return o1.getSourceName().compareTo(o2.getSourceName());
			}
			
		});
		this.addColumn(diagramGeneNameColumn, "Diagram Source");	
		
		PairwiseInteractorColumn pairwiseInteractorColumn= new PairwiseInteractorColumn();
		pairwiseInteractorColumn.setSortable(true);
		sorter.setComparator(pairwiseInteractorColumn, new Comparator<PairwiseTableEntity>() {
			@Override
			public int compare(PairwiseTableEntity o1, PairwiseTableEntity o2) {
				return o1.getInteractorName().compareTo(o2.getInteractorName());
			}
		});
		this.addColumn(pairwiseInteractorColumn, "Pairwise Interactor");
		
		OverlayValueColumn overlayValueColumn = new OverlayValueColumn();
		this.addColumn(overlayValueColumn, "Overlay Value");	
		
		PairwiseRelationshipColumn pairwiseRelationshipColumn = new PairwiseRelationshipColumn();
		pairwiseRelationshipColumn.setSortable(true);
		sorter.setComparator(pairwiseRelationshipColumn, new Comparator<PairwiseTableEntity>() {
			@Override
			public int compare(PairwiseTableEntity o1, PairwiseTableEntity o2) {
				return o1.getPosOrNeg().compareTo(o2.getPosOrNeg());
			}
		});
		this.addColumn(pairwiseRelationshipColumn, "Pos/Neg");
		
		PairwiseSourceColumn pairwiseSourceColumn = new PairwiseSourceColumn();
		pairwiseSourceColumn.setSortable(true);
		sorter.setComparator(pairwiseSourceColumn, new Comparator<PairwiseTableEntity>() {
			@Override
			public int compare(PairwiseTableEntity o1, PairwiseTableEntity o2) {
				return o1.getDataDesc().compareTo(o2.getDataDesc());
			}
		});
		this.addColumn(pairwiseSourceColumn, "Interaction Source");
		this.setColumnWidth(pairwiseSourceColumn, 200+"px");
		
		this.addCellPreviewHandler(new CellPreviewEvent.Handler<PairwiseTableEntity>() {
            @Override
            public void onCellPreview(final CellPreviewEvent<PairwiseTableEntity> event) {
            	if(event.getNativeEvent().getType().equals("click")) {
            		handler.onRowClicked(event.getValue());
            	}
                if (!event.getNativeEvent().getType().equals("mouseover")) return;
                Element cellElement = event.getNativeEvent().getEventTarget().cast();
                PairwiseTableEntity model = (PairwiseTableEntity) PairwiseResultsTable.this.getValueKey(event.getValue());
                cellElement.setTitle(PairwiseResultsTable.this.getColumn(event.getColumn()).getValue(model)+"");
            }
        });
		
		selectionModel = new SingleSelectionModel<>();
		this.setSelectionModel(selectionModel);
		
		//Pager setup
		pager.setDisplay(this);
		pager.setPageSize(PAGE_SIZE);
		
		this.setRowCount(entities.size(), true);
		
		this.redraw();
	}
	
	public void updateSorter() {
		sorter.setList(provider.getList());
	}
	
	public void selectRow(PairwiseTableEntity entity, boolean select) {
		selectionModel.setSelected(entity, select);
	}

	public void resetSelection() {
		if(selectionModel.getSelectedObject() == null)return;
		selectionModel.setSelected(selectionModel.getSelectedObject(), false);
	}
}
