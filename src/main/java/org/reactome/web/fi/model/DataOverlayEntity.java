package org.reactome.web.fi.model;

public class DataOverlayEntity {

	private String identifier;
	private Double value;
	private String type;

	
	public DataOverlayEntity(String identifier, Double value, String type) {
		this.identifier = identifier;
		this.value = value;
		this.type = type;
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
}
