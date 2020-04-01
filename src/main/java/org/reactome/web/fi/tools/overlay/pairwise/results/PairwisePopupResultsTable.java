package org.reactome.web.fi.tools.overlay.pairwise.results;

import java.util.Comparator;
import java.util.List;

import org.reactome.web.fi.common.IDGPager;
import org.reactome.web.fi.tools.overlay.pairwise.model.PairwiseTableEntity;
import org.reactome.web.fi.tools.overlay.pairwise.results.columns.DiagramGeneNameColumn;
import org.reactome.web.fi.tools.overlay.pairwise.results.columns.OverlayValueColumn;
import org.reactome.web.fi.tools.overlay.pairwise.results.columns.PairwiseInteractorColumn;
import org.reactome.web.fi.tools.overlay.pairwise.results.columns.PairwiseRelationshipColumn;
import org.reactome.web.fi.tools.overlay.pairwise.results.columns.PairwiseSourceColumn;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.ListDataProvider;

/**
 * 
 * @author brunsont
 *
 */
public class PairwisePopupResultsTable extends DataGrid<PairwiseTableEntity>{
	
	public final static Integer PAGE_SIZE = 10;
	
	private ListHandler<PairwiseTableEntity> sorter;
	private ListDataProvider<PairwiseTableEntity> provider;
	
	
	public PairwisePopupResultsTable(List<PairwiseTableEntity> entities, ListDataProvider<PairwiseTableEntity> provider, IDGPager pager) {
		
		super(PAGE_SIZE);
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
                if (!event.getNativeEvent().getType().equals("mouseover")) return;
                Element cellElement = event.getNativeEvent().getEventTarget().cast();
                PairwiseTableEntity model = (PairwiseTableEntity) PairwisePopupResultsTable.this.getValueKey(event.getValue());
                cellElement.setTitle(PairwisePopupResultsTable.this.getColumn(event.getColumn()).getValue(model)+"");
            }
        });
		
		//Pager setup
		pager.setDisplay(this);
		pager.setPageSize(PAGE_SIZE);
		
		this.setRowCount(entities.size(), true);
		
		this.redraw();
	}
	
	public void updateSorter() {
		sorter.setList(provider.getList());
	}
}
