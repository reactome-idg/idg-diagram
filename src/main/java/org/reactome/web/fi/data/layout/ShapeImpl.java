package org.reactome.web.fi.data.layout;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.Shape;

public class ShapeImpl implements Shape{

	private Coordinate c;
	private Double r;
	private String type;
	private boolean empty;
	
	public ShapeImpl(Coordinate c, Double r, String type, boolean empty) {
		this.c = c;
		this.r = r;
		this.type = type;
		this.empty = empty;
	}
	
	@Override
	public Coordinate getC() {
		return c;
	}
	
	@Override
	public Double getR() {
		return r;
	}
	
	@Override
	public String getType() {
		return type;
	}
	
	@Override
	public Boolean getEmpty() {
		return empty;
	}
	
	@Override
	public Coordinate getA() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Coordinate getB() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getR1() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getS() {
		// TODO Auto-generated method stub
		return null;
	}
}
