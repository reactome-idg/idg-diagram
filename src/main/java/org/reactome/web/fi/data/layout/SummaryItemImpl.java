package org.reactome.web.fi.data.layout;

import org.reactome.web.diagram.data.layout.Shape;
import org.reactome.web.diagram.data.layout.SummaryItem;

public class SummaryItemImpl implements SummaryItem{

	private String type = "TR";
	private Shape shape;
	private Boolean pressed;
	private Integer number;
	private Boolean isHit;
	
	public SummaryItemImpl(Shape shape, Integer number) {
		this.shape = shape;
		this.number = number;
	}
	
	@Override
	public String getType() {
		return type;
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

}
