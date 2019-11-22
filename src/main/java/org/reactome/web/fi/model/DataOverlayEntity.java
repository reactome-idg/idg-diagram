package org.reactome.web.fi.model;

public class DataOverlayEntity {

	private String identifier;
	private Double value;
	private String type;
	private String tissue;

	
	public DataOverlayEntity(String identifier, Double value, String type, String tissue) {
		this.identifier = identifier;
		this.value = value;
		this.type = type;
		this.tissue = tissue;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public Double getValue() {
		return value;
	}
	
	public void setValue(Double value) {
		this.value = value;
	}

	public String getExpressionType() {
		return type;
	}

	public void setExpressionType(String type) {
		this.type = type;
	}

	public String getTissue() {
		return tissue;
	}

	public void setTissue(String tissue) {
		this.tissue = tissue;
	}
	
}
