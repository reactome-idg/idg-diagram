package org.reactome.web.fi.tools.popup;

import org.reactome.web.fi.tools.popup.tables.models.DrugTargetResult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * 
 * @author brunsont
 *
 */
public class DrugTargetContextPanel extends DialogBox{

	private final String DRUG_CENTRAL_URL = "http://drugcentral.org/?q=";
	
	public DrugTargetContextPanel(DrugTargetResult drug) {
		setStyleName(RESOURCES.getCSS().nodePopup());
		setAutoHideEnabled(true);
		setModal(false);
		
		setTitlePanel(drug.getDrugName());
		
		FlowPanel main = new FlowPanel();
		main.setStyleName(RESOURCES.getCSS().mainPanel());
		main.add(getTablePanel(drug));
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

	private FlexTable getTablePanel(DrugTargetResult drug) {
		FlexTable table = new FlexTable();
		
		table.setStyleName(RESOURCES.getCSS().table());
		
		table.setText(0, 0, "Drug Central: ");
		table.setWidget(0, 1, getDrugCentralLink(drug.getDrugName()));
		table.getFlexCellFormatter().setColSpan(0, 0, 3);
		
		return table;
	}
	
	
	private Anchor getDrugCentralLink(String name) {
		String link = DRUG_CENTRAL_URL + name;
		Anchor result = new Anchor(new SafeHtmlBuilder()
				.appendEscapedLines("Go!").toSafeHtml(),
				link, "_blank");
		result.setStyleName(RESOURCES.getCSS().linkAnchor());
		result.getElement().appendChild(new Image(RESOURCES.linkOut()).getElement());
		return result;
	}


	public static Resources RESOURCES;
	static {
		RESOURCES = GWT.create(Resources.class);
		RESOURCES.getCSS().ensureInjected();
	}
	
	public interface Resources extends ClientBundle{
		@Source(ResourceCSS.CSS)
		ResourceCSS getCSS();
		
		@Source("images/external_link_icon.gif")
		ImageResource linkOut();
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
