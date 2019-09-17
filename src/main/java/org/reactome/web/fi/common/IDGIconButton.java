package org.reactome.web.fi.common;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * 
 * @author brunsont
 *
 */
public class IDGIconButton extends Button{
	private FlowPanel fp;
	private Image image;
	private InlineLabel label;
	
	public IDGIconButton(String text, ImageResource imageResource) {
		fp = new FlowPanel();
	
		if(imageResource!=null) {
			image = new Image(imageResource);
			fp.add(image);
		}
		
		if(text!=null && !text.isEmpty()) {
			label = new InlineLabel(text);
			fp.add(label);
		}
		
		updateHTML();
	}
	
	public IDGIconButton(ImageResource imageResource, String style, String tooltip) {
		this(null, imageResource);
		this.setStyleName(style);
		this.setTitle(tooltip);
		
	}
	
	public void setButtonImage(ImageResource imageResource) {
		image.setResource(imageResource);
	}
	
	private void updateHTML() {
		SafeHtml safeHtml = SafeHtmlUtils.fromSafeConstant(fp.toString());
		this.setHTML(safeHtml);
	}
}
