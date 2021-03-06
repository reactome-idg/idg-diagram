package org.reactome.web.fi.common;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

/**
 * 
 * @author brunsont
 *
 */
public class CommonButton extends Button{

	public CommonButton(String name, ClickHandler handler) {
		setText(name);
		addClickHandler(handler);
	}
	
	public CommonButton(String name, String style, ClickHandler handler) {
		setText(name);
		setStyleName(style);
		addClickHandler(handler);
	}
	
}
