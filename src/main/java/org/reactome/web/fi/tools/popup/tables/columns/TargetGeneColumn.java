package org.reactome.web.fi.tools.popup.tables.columns;

import org.reactome.web.fi.tools.popup.tables.models.DrugTargetResult;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;

/**
 * 
 * @author brunsont
 *
 */
public class TargetGeneColumn  extends Column<DrugTargetResult, String>{
	
	public TargetGeneColumn() {
		super(new TextCell());
	}

	/**
	 * Display term name or uniprot if gene name is null
	 */
	@Override
	public String getValue(DrugTargetResult object) {
		return object.getGeneName() != null ? object.getGeneName():object.getUniprot();
	}
	
}
