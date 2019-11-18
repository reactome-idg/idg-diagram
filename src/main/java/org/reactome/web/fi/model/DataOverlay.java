package org.reactome.web.fi.model;

import java.util.ArrayList;
import java.util.List;

public class DataOverlay {

	private String minValue;
	private String maxValue;
	private boolean isDiscrete;
	private List<DataOverlayEntity> dataOverlayEntities;
	
	public DataOverlay(boolean isDiscrete) {
		this.isDiscrete = isDiscrete;
		dataOverlayEntities = new ArrayList<>();
	}

	public String getMinValue() {
		return minValue;
	}

	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}

	public String getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(String maxValue) {
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
}
