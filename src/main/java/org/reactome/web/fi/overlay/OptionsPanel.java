package org.reactome.web.fi.overlay;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public class OptionsPanel extends Composite implements ClickHandler{
	
	public interface Handler {
		void onOverlaySelected();
		void onBackSelected();
		void onCancelSelected();
	}

	private Button cancelButton;
	private Button overlayButton;
	private Button backButton;
	
	private Handler handler;
	private FlowPanel main;
	private FlowPanel options;
	private ScrollPanel scrollPanel;
	
	public OptionsPanel(Handler handler) {
		this.handler = handler;
		
		main = new FlowPanel();
		main.getElement().getStyle().setHeight(150, Unit.PX);
		scrollPanel = new ScrollPanel();
		scrollPanel.setStyleName(RESOURCES.getCSS().optionsPanel());
		scrollPanel.add(getOptions());
		main.add(scrollPanel);
		main.add(confirmationButtonPanel());
		initWidget(main);
		
	}
	
	private FlowPanel getOptions() {
		options = new FlowPanel();
		options.setStyleName(RESOURCES.getCSS().optionsPanel());
		
		Label optionsLabel = new Label("Overlay Options:");
		optionsLabel.setStyleName(RESOURCES.getCSS().optionsLabel());
		options.add(optionsLabel);
		Label lbl = new Label("No overlay options available. \n Click \"Overlay!\" to perform overlay!");
		options.add(lbl);
//		Label lbl = new Label("Expression Types");
//		lbl.setStyleName(RESOURCES.getCSS().label());
//		CheckBox box = new CheckBox();
//		box.setName("expressionTypes");
//		box.setText("Option 1");
//		CheckBox box2 = new CheckBox();
//		box2.setName("expressionTypes");
//		box2.setText("Option 2");
//		Label lbl2 = new Label("Tissue Types");
//		lbl2.setStyleName(RESOURCES.getCSS().label());
//		CheckBox box3 = new CheckBox();
//		box3.setName("tissues");
//		box3.setText("Option 1");
//		CheckBox box4 = new CheckBox();
//		box4.setName("tissues");
//		box4.setText("Option 2");
//		
//		options.add(lbl);
//		options.add(box);
//		options.add(box2);
//		options.add(lbl2);
//		options.add(box3);
//		options.add(box4);
		
		return options;
	}

	private FlowPanel confirmationButtonPanel() {
		backButton = new Button("Back");
		backButton.addClickHandler(this);
		cancelButton = new Button("Cancel");
		cancelButton.addClickHandler(this);
		overlayButton = new Button("Overlay!");
		overlayButton.addClickHandler(this);
		FlowPanel panel = new FlowPanel();
		panel.add(backButton);
		panel.add(cancelButton);
		panel.add(overlayButton);
		panel.setStyleName(RESOURCES.getCSS().buttonPanel());
		return panel;
	}
	
	@Override
	public void onClick(ClickEvent event) {
		Button btn = (Button) event.getSource();
		
		if(btn.equals(this.cancelButton))
			handler.onCancelSelected();
		else if(btn.equals(this.backButton))
			handler.onBackSelected();
		else if(btn.equals(this.overlayButton))
			handler.onOverlaySelected();
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
	
	@CssResource.ImportedWithPrefix("idg-diagram-OverlayInfoPanel")
	public interface ResourceCSS extends CssResource{
		String CSS = "org/reactome/web/fi/overlay/OverlayOptionsPanel.css";
		
		String buttonPanel();
		
		String optionsPanel();
	
		String optionsLabel();
		
		String label();
	}
	
}
