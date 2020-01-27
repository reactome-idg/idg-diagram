package org.reactome.web.fi.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reactome.web.fi.data.overlay.model.DataOverlayProperties;

/**
 * 
 * @author brunsont
 *
 */
public class DataOverlay {

	private Double minValue;
	private Double maxValue;
	private boolean isDiscrete;
	private Map<String, List<DataOverlayEntity>> uniprotToEntitiesMap;
	private List<String> legendTypes;
	private List<String> tissueTypes;
	private int column;
	private Map<String, Double> identifierValueMap;
	private String eType;
	private DataOverlayProperties properties;
	
	public DataOverlay(DataOverlayProperties properties) {
		this.properties = properties;
		column = 0;
	}

	public Double getMinValue() {
		return minValue;
	}

	public DataOverlayProperties getOverlayProperties() {
		return this.properties;
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
	
	/**
	 * Helper method to resets IdentifierValueMap to just represent 
	 * currently displayed tissue based on current column.
	 */
	public void updateIdentifierValueMap() {
		if(this.tissueTypes == null || this.tissueTypes.size()<=1) return;
    	
		Map<String, Double> identifierValueMap = new HashMap<>();
    	this.uniprotToEntitiesMap.forEach((k,v) ->{
			v.forEach((l) -> {
				if(tissueTypes.get(this.column) == l.getTissue())
					identifierValueMap.put(k, l.getValue());
			});
		});
        this.setIdentifierValueMap(identifierValueMap);
	}
}
