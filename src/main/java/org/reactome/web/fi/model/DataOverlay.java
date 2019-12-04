package org.reactome.web.fi.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author brunsont
 *
 */
public class DataOverlay {

	private Double minValue;
	private Double maxValue;
	private boolean isDiscrete;
	private OverlayDataType overlayType;
	private Map<String, List<DataOverlayEntity>> uniprotToEntitiesMap;
	private List<String> legendTypes;
	private List<String> tissueTypes;
	private int column;
	private Map<String, Double> identifierValueMap;
	private String eType;
	
	public DataOverlay() {
		column = 0;
	}

	public Double getMinValue() {
		return minValue;
	}

	public void setMinValue(Double minValue) {
		this.minValue = minValue;
	}

	public Double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}

	public boolean isDiscrete() {
		return isDiscrete;
	}

	public void setDiscrete(boolean isDiscrete) {
		this.isDiscrete = isDiscrete;
	}

	public List<String> getLegendTypes() {
		return legendTypes;
	}

	public void setLegendTypes(List<String> legendTypes) {
		this.legendTypes = legendTypes;
	}

	public Map<String, Double> getIdentifierValueMap() {
		return identifierValueMap;
	}

	public void setIdentifierValueMap(Map<String, Double> identifierValueMap) {
		this.identifierValueMap = identifierValueMap;
	}

	public OverlayDataType getOverlayType() {
		return overlayType;
	}

	public void setOverlayType(OverlayDataType overlayType) {
		this.overlayType = overlayType;
	}

	public String getEType() {
		return eType;
	}

	public void setEType(String eType) {
		this.eType = eType;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public List<String> getTissueTypes() {
		return tissueTypes;
	}

	public void setTissueTypes(List<String> tissueTypes) {
		this.tissueTypes = tissueTypes;
	}
	
	public Map<String, List<DataOverlayEntity>> getUniprotToEntitiesMap() {
		return uniprotToEntitiesMap;
	}

	public void setUniprotToEntitiesMap(Map<String, List<DataOverlayEntity>> uniprotToEntitiesMap) {
		this.uniprotToEntitiesMap = uniprotToEntitiesMap;
	}
}
