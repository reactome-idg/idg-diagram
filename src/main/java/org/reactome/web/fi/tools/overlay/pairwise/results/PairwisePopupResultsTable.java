package org.reactome.web.fi.tools.overlay.pairwise.results;

import java.util.List;

import org.reactome.web.fi.tools.overlay.pairwise.PairwiseTableEntity;
import org.reactome.web.fi.tools.overlay.pairwise.results.columns.DiagramGeneNameColumn;
import org.reactome.web.fi.tools.overlay.pairwise.results.columns.OverlayValueColumn;
import org.reactome.web.fi.tools.overlay.pairwise.results.columns.PairwiseInteractorColumn;
import org.reactome.web.fi.tools.overlay.pairwise.results.columns.PairwiseRelationshipColumn;
import org.reactome.web.fi.tools.overlay.pairwise.results.columns.PairwiseSourceColumn;

import com.google.gwt.user.cellview.client.DataGrid;

/**
 * 
 * @author brunsont
 *
 */
public class PairwisePopupResultsTable extends DataGrid<PairwiseTableEntity>{
	public final static Integer PAGE_SIZE = 10;
	
	
	public PairwisePopupResultsTable(List<PairwiseTableEntity> entities) {
		super(PAGE_SIZE);
		this.setRowData(0, entities);
		this.setAutoHeaderRefreshDisabled(true);
		this.setWidth("100%");
		this.setVisible(true);
		this.setHeight("200px");
		
		
		this.addColumn(new DiagramGeneNameColumn(), "Diagram Source");
		this.addColumn(new PairwiseInteractorColumn(), "Pairwise Interactor");
		this.addColumn(new PairwiseSourceColumn(), "Interaction Source");
		this.addColumn(new PairwiseRelationshipColumn(), "Pos/Neg");
		this.addColumn(new OverlayValueColumn(), "Overlay Value");
		this.redraw();
	}
}
