package org.reactome.web.fi.data.overlay.model;

public class OverlayProperties {

	String valueType;
	String unit;
	String sex;
	
	public OverlayProperties(String valueType, String unit, String sex) {
		this.valueType = valueType;
		this.unit = unit;
		this.sex = sex;
	}

	public String getValueType() {
		return valueType;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}
}
