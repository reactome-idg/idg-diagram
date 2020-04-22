package org.reactome.web.fi.client.popups;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * 
 * @author brunsont
 *
 */
public class FIViewInfoPopup extends PopupPanel{

	private HTML htmlLabel;
	
	public FIViewInfoPopup() {
		super(true);
		this.setStyleName(RESOURCES.getCSS().popup());
	}
	
	public HTML getHtmlLabel() {
		return htmlLabel;
	}

	public void setHtmlLabel(HTML htmlLabel, int x, int y) {
		this.htmlLabel = htmlLabel;
		this.setWidget(htmlLabel);
		this.setPopupPosition(x + 10, y + 10);
	}
	
	/**
	 * Sets label based on passed in node properties and sets location
	 * @param id
	 * @param name
	 * @param x
	 * @param y
	 */
	public void setNodeLabel(String id, String name, int x, int y) {
		HTML html = new HTML(new SafeHtmlBuilder()
				.appendEscapedLines(name + " (" + id + ")")
				.toSafeHtml());
		htmlLabel = html;
		this.setWidget(htmlLabel);
		this.setPopupPosition(x+10, y+10);
		this.show();
	}
	
	public void setNodeLabel(String id, int x, int y) {
		HTML html = new HTML(new SafeHtmlBuilder()
				.appendEscapedLines(id)
				.toSafeHtml());
		htmlLabel = html;
		this.setWidget(htmlLabel);
		this.setPopupPosition(x+10, y+10);
		this.show();
	}
	
	public void setEdgeLabel(String source, String target, int x, int y) {
		HTML html = new HTML(new SafeHtmlBuilder()
				.appendEscapedLines(source + " - " + target)
				.toSafeHtml());
		htmlLabel =html;
		this.setWidget(htmlLabel);
		this.setPopupPosition(x+10, y+10);
		this.show();
	}
	
	/**
	 * Add set of strings to label.
	 * @param set
	 * @param x
	 * @param y
	 */
	public void setEdgeLabel(String edge, int x, int y) {
		HTML html = new HTML(new SafeHtmlBuilder().appendEscapedLines(edge).toSafeHtml());
		htmlLabel = html;
		this.setWidget(htmlLabel);
		this.setPopupPosition(x+10, y+10);
		this.show();
	}
	
	public static Resources RESOURCES;
	static {
		RESOURCES = GWT.create(Resources.class);
		RESOURCES.getCSS().ensureInjected();
	}
	
	public interface Resources extends ClientBundle {
		@Source(ResourceCSS.CSS)
		ResourceCSS getCSS();
	}
	
	@CssResource.ImportedWithPrefix("FIViewInfoPopup")
	public interface ResourceCSS extends CssResource {
		String CSS = "org/reactome/web/fi/client/popups/FIViewInfoPopup.css";
		
		String popup();
		
	}
	
}
