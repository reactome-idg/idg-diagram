package org.reactome.web.fi.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;

public class RemoveButtonPopup extends PopupPanel{

	public RemoveButtonPopup() {
		FlowPanel panel = new FlowPanel();
		Button removeButton = new Button("Remove");
		removeButton.addClickHandler(e -> removeButtonClicked());
		panel.add(removeButton);
		
		setWidget(panel);
	}
	
	public void setZIndex(int zIndex) {
		this.getElement().getStyle().setZIndex(zIndex);
	}

	private void removeButtonClicked() {
		Window.alert("Remove NODE!!");
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
	
	@CssResource.ImportedWithPrefix("idg-removeButtonPopup")
	public interface ResourceCSS extends CssResource{
		String CSS = "org/reactome/web/fi/common/RemoveButtonPopup.css";
		
	}
	
}
