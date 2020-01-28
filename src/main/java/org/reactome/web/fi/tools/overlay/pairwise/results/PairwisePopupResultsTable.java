package org.reactome.web.fi.tools.overlay.pairwise.results;

import java.util.List;

import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseEntity;

import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;

public class PairwisePopupResultsTable extends DataGrid<PairwiseEntity>{
	public final static Integer PAGE_SIZE = 10;
	
	
	public PairwisePopupResultsTable(List<PairwiseEntity> entities) {
		super(PAGE_SIZE);
		this.setRowData(entities);
		this.setAutoHeaderRefreshDisabled(true);
		this.setWidth("100%");
		this.setVisible(true);
		this.setHeight("200px");
		
		//Add diagram source as first column
		TextColumn<PairwiseEntity> diagramSourceUniprot = new TextColumn<PairwiseEntity>() {
			@Override
			public String getValue(PairwiseEntity object) {
				return object.getGene();
			}
		};
		this.addColumn(diagramSourceUniprot, "Diagram Source");
		this.redraw();
	}
}
