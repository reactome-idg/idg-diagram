package org.reactome.web.fi.tools.popup.tables.columns;

import org.reactome.web.fi.tools.popup.tables.models.DrugTargetResult;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;

/**
 * 
 * @author brunsont
 *
 */
public class DrugNameColumn extends Column<DrugTargetResult, String>{

	public DrugNameColumn() {
		super(new TextCell());
	}
	
	@Override
	public String getValue(DrugTargetResult object) {
		return object.getDrugName();
	}
	
}
