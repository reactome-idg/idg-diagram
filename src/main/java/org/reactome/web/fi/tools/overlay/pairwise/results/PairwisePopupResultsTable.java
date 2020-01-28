package org.reactome.web.fi.tools.overlay.pairwise.results;

import java.util.List;

import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseEntity;
import org.reactome.web.fi.tools.overlay.pairwise.PairwiseTableEntity;
import org.reactome.web.fi.tools.overlay.pairwise.results.columns.DiagramGeneNameColumn;
import org.reactome.web.fi.tools.overlay.pairwise.results.columns.PairwiseInteractorColumn;

import com.google.gwt.user.cellview.client.DataGrid;

public class PairwisePopupResultsTable extends DataGrid<PairwiseTableEntity>{
	public final static Integer PAGE_SIZE = 10;
	
	
	public PairwisePopupResultsTable(List<PairwiseTableEntity> entities) {
		super(PAGE_SIZE);
		this.setRowData(entities);
		this.setAutoHeaderRefreshDisabled(true);
		this.setWidth("100%");
		this.setVisible(true);
		this.setHeight("200px");
		
//		//Add diagram source as first column
//		TextColumn<PairwiseEntity> diagramSourceUniprot = new TextColumn<PairwiseEntity>() {
//			@Override
//			public String getValue(PairwiseEntity object) {
//				return object.getGene();
//			}
//		};
		this.addColumn(new DiagramGeneNameColumn(), "Diagram Source");
		this.addColumn(new PairwiseInteractorColumn(), "Pairwise Interactor");
		
		this.redraw();
	}
}
