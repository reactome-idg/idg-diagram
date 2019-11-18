package org.reactome.web.fi.model;

import java.util.ArrayList;
import java.util.List;

public class DataOverlay {

	private Double minValue;
	private Double maxValue;
	private boolean isDiscrete;
	private List<DataOverlayEntity> dataOverlayEntities;
	private List<String> discreteTypes;
	
	public DataOverlay() {
		dataOverlayEntities = new ArrayList<>();
		discreteTypes = new ArrayList<>();
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
	
}
