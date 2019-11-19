package org.reactome.web.fi.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataOverlay {

	private Double minValue;
	private Double maxValue;
	private boolean isDiscrete;
	private List<DataOverlayEntity> dataOverlayEntities;
	private List<String> discreteTypes;
	private Map<String, Double> identifierValueMap;
	
	public DataOverlay() {
		dataOverlayEntities = new ArrayList<>();
		discreteTypes = new ArrayList<>();
		identifierValueMap = new HashMap<>();
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

	public List<DataOverlayEntity> getDataOverlayEntities() {
		return dataOverlayEntities;
	}

	public void addDataOverlayEntity(DataOverlayEntity dataOverlayEntity) {
		this.dataOverlayEntities.add(dataOverlayEntity);
	}

	public List<String> getDiscreteTypes() {
		return discreteTypes;
	}

	public void setDiscreteTypes(List<String> discreteTypes) {
		this.discreteTypes = discreteTypes;
	}

	public Map<String, Double> getIdentifierValueMap() {
		return identifierValueMap;
	}

	public void setIdentifierValueMap(Map<String, Double> identifierValueMap) {
		this.identifierValueMap = identifierValueMap;
	}
	
	
}
