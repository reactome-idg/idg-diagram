package org.reactome.web.fi.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataOverlay {

	private Double minValue;
	private Double maxValue;
	private boolean isDiscrete;
	private OverlayDataType overlayType;
	private List<DataOverlayEntity> dataOverlayEntities;
	private List<String> types;
	private Map<String, Double> identifierValueMap;
	private String eType;
	
	public DataOverlay() {
		dataOverlayEntities = new ArrayList<>();
		types = new ArrayList<>();
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

	public List<String> getTypes() {
		return types;
	}

	public void setTypes(List<String> types) {
		this.types = types;
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
	
}
