package org.reactome.web.fi.data.layout;

import org.reactome.web.diagram.data.layout.Shape;

/**
 * 
 * @author brunsont
 *
 */
public class DrugTargetItemImpl implements DrugTargetItem{

	private String type = "DT";
	private Shape shape;
	private Boolean pressed;
	private Integer number;
	private Boolean isHit;
	
	public DrugTargetItemImpl(Shape shape, Integer number) {
		this.shape = shape;
		this.number = number;
	}

	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public Shape getShape() {
		return this.shape;
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
		return this.isHit;
	}
}
