package org.reactome.web.fi.tools.overlay.pairwise.results;

import java.util.List;

import org.reactome.web.fi.common.IDGPager;
import org.reactome.web.fi.tools.overlay.pairwise.PairwiseTableEntity;
import org.reactome.web.fi.tools.overlay.pairwise.results.columns.DiagramGeneNameColumn;
import org.reactome.web.fi.tools.overlay.pairwise.results.columns.OverlayValueColumn;
import org.reactome.web.fi.tools.overlay.pairwise.results.columns.PairwiseInteractorColumn;
import org.reactome.web.fi.tools.overlay.pairwise.results.columns.PairwiseRelationshipColumn;
import org.reactome.web.fi.tools.overlay.pairwise.results.columns.PairwiseSourceColumn;

import com.google.gwt.dom.client.Element;
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
	
	
	public PairwisePopupResultsTable(List<PairwiseTableEntity> entities, ListDataProvider<PairwiseTableEntity> provider, IDGPager pager) {
		
		super(PAGE_SIZE);
		this.setRowData(0, entities);
		this.setAutoHeaderRefreshDisabled(true);
		this.setWidth("100%");
		this.setVisible(true);
		this.setHeight("200px");
		
		this.addColumn(new DiagramGeneNameColumn(), "Diagram Source");
		this.addColumn(new PairwiseInteractorColumn(), "Pairwise Interactor");
		this.addColumn(new OverlayValueColumn(), "Overlay Value");
		this.addColumn(new PairwiseRelationshipColumn(), "Pos/Neg");
		this.addColumn(new PairwiseSourceColumn(), "Interaction Source");
		
		this.addCellPreviewHandler(new CellPreviewEvent.Handler<PairwiseTableEntity>() {
            @Override
            public void onCellPreview(final CellPreviewEvent<PairwiseTableEntity> event) {
                if (!event.getNativeEvent().getType().equals("mouseover")) return;
                Element cellElement = event.getNativeEvent().getEventTarget().cast();
                PairwiseTableEntity model = (PairwiseTableEntity) PairwisePopupResultsTable.this.getVisibleItem(event.getIndex());
                cellElement.setTitle(PairwisePopupResultsTable.this.getColumn(event.getColumn()).getValue(model)+"");
            }
        });
				
		//ListDataProvider setup
		provider.addDataDisplay(this);
		provider.setList(entities);
		
		//Pager setup
		pager.setDisplay(this);
		pager.setPageSize(PAGE_SIZE);
		
		this.setRowCount(entities.size(), true);
		
		this.redraw();
	}
}
