package org.reactome.web.fi.tools.popup;

import org.reactome.web.fi.data.model.drug.DrugTargetEntity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;

public class DrugTargetContextPanel extends DialogBox{

	public DrugTargetContextPanel(DrugTargetEntity target) {
		setStyleName(RESOURCES.getCSS().nodePopup());
		setAutoHideEnabled(true);
		setModal(false);
		
		setTitlePanel(target.getDrug());
		
		FlowPanel main = new FlowPanel();
		main.setStyleName(RESOURCES.getCSS().mainPanel());
		main.add(getTablePanel(target));
		setWidget(main);
	}

	private void setTitlePanel(String drug) {
		FlowPanel fp = new FlowPanel();
		InlineLabel title = new InlineLabel(drug);
		fp.add(title);
		
		SafeHtml safe = SafeHtmlUtils.fromTrustedString(fp.toString());
		getCaption().setHTML(safe);
		getCaption().asWidget().setStyleName(RESOURCES.getCSS().header());
	}

	private FlexTable getTablePanel(DrugTargetEntity target) {
		FlexTable table = new FlexTable();
		
		table.setStyleName(RESOURCES.getCSS().table());
		
		table.setText(0, 0, "Activity: ");
		table.setText(0, 1, NumberFormat.getFormat("#.##E0").format(target.getActivityValue()));
		table.getFlexCellFormatter().setColSpan(0, 0, 3);
		
		table.setText(1, 0, "Activity Type: ");
		table.setText(1, 1, target.getActivityType());
		table.getFlexCellFormatter().setColSpan(1, 0, 3);
		
		table.setText(2, 0, "Action Type: ");
		table.setText(2, 1, target.getActionType().toLowerCase());
		table.getFlexCellFormatter().setColSpan(2, 0, 3);
		
		return table;
	}
	
	
	public static Resources RESOURCES;
	static {
		RESOURCES = GWT.create(Resources.class);
		RESOURCES.getCSS().ensureInjected();
	}
	
	public interface Resources extends ClientBundle{
		@Source(ResourceCSS.CSS)
		ResourceCSS getCSS();
	}
	
	@CssResource.ImportedWithPrefix("idg-DrugTargetContextPanel")
	public interface ResourceCSS extends CssResource {
		String CSS = "org/reactome/web/fi/tools/popup/DrugTargetContextPanel.css";
	
		String nodePopup();
		
		String header();
		
		String mainPanel();
		
		String label();
		
		String table();
		
		String linkAnchor();
	}
}
