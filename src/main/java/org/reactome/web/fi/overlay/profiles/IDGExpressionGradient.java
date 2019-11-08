package org.reactome.web.fi.overlay.profiles;

import java.util.Map;

import org.reactome.web.diagram.util.gradient.ThreeColorGradient;

/**
 * 
 * @author brunsont
 *
 * This class is used to override the AnalysisColours expression gradient for use in overlays
 *
 */
public class IDGExpressionGradient extends ThreeColorGradient{

	private Map<Double, String> colourMap;
	
	public IDGExpressionGradient(String hexFrom, String hexStop, String hexTo) {
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
