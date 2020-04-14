package org.reactome.web.fi.tools.popup.tables.columns;

import org.reactome.web.fi.tools.popup.tables.models.DrugTargetResult;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;

public class ActionTypeColumn extends Column<DrugTargetResult, String>{

	public ActionTypeColumn() {
		super(new TextCell());
	}
	
	@Override
	public String getValue(DrugTargetResult object) {
		return object.getActionType();
	}
	
}
