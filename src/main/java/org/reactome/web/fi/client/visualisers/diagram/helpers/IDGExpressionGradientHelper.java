package org.reactome.web.fi.client.visualisers.diagram.helpers;

import java.util.Map;

import org.reactome.web.diagram.util.gradient.ThreeColorGradient;

import com.google.gwt.core.client.GWT;

public class IDGExpressionGradientHelper extends ThreeColorGradient{

	private Map<Double, String> colourMap;
	
	public IDGExpressionGradientHelper(String hexFrom, String hexStop, String hexTo) {
		super("#FFFF50", null, "#FFFFCA");
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getColor(double p) {
		return colourMap.get((double) Math.round(p));
	}

	@Override
	public String getColor(double point, double min, double max) {
		return this.getColor(point);
	}

	public void setColourMap(Map<Double, String> colourMap) {
		this.colourMap = colourMap;
	}
}
