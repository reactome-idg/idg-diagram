package org.reactome.web.fi.client.visualisers.diagram.helpers;

import java.util.Map;

import org.reactome.web.diagram.util.gradient.ThreeColorGradient;

import com.google.gwt.core.client.GWT;

public class IDGExpressionGradientHelper extends ThreeColorGradient{

	Map<String, String> colourMap;
	
	public IDGExpressionGradientHelper(String hexFrom, String hexStop, String hexTo) {
		super("#FFFF50", null, "#FFFFCA");
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getColor(double p) {
		String pString = (int)p+"";
		GWT.log(colourMap.get(pString));
		return colourMap.get(pString);
	}

	@Override
	public String getColor(double point, double min, double max) {
		GWT.log(point+"");
		return this.getColor(point);
	}

	public void setColourMap(Map<String, String> colourMap) {
		this.colourMap = colourMap;
	}

}
