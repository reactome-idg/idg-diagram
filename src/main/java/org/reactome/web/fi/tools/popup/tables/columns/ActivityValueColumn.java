package org.reactome.web.fi.tools.popup.tables.columns;

import org.reactome.web.fi.tools.popup.tables.models.DrugTargetResult;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.cellview.client.Column;

/**
 * 
 * @author brunsont
 *
 */
public class ActivityValueColumn extends Column<DrugTargetResult, String>{
	
	public ActivityValueColumn() {
		super(new TextCell());
	}
	
	@Override
	public String getValue(DrugTargetResult object) {
		return object.getActivityValue() == null ? "" : NumberFormat.getFormat("#.##E0").format(object.getActivityValue());
	}

}
