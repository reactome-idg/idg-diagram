package org.reactome.web.fi.data.layout;

import org.reactome.web.diagram.data.layout.Shape;
import org.reactome.web.diagram.data.layout.SummaryItem;

/**
 * 
 * @author brunsont
 *
 */
public class SummaryItemImpl implements SummaryItem{

	private String type = "TR";
	private Shape shape;
	private Boolean pressed;
	private Integer number;
	private Boolean isHit;
	private String label;
	
	public SummaryItemImpl(Shape shape, Integer number) {
		this.shape = shape;
		this.number = number;
	}
	
	@Override
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Override
	public Shape getShape() {
		return shape;
	}
	
	public void setShape(Shape shape) {
		this.shape = shape;
	}

	@Override
	public Boolean getPressed() {
		return pressed;
	}

	@Override
	public void setPressed(Boolean pressed) {
		this.pressed = pressed;
	}

	@Override
	public Integer getNumber() {
		return number;
	}

	@Override
	public void setNumber(Integer number) {
		this.number = number;
	}

	@Override
	public void setHit(Boolean hit) {
		this.isHit = hit;
	}

	@Override
	public Boolean getHit() {
		return isHit;
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getLabel() {
		return label;
	}

}
