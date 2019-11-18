package org.reactome.web.fi.model;

public class DataOverlayEntity {

	private String identifier;
	private Double value;

	
	public DataOverlayEntity(String identifier, Double value) {
		this.identifier = identifier;
		this.value = value;

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
}
