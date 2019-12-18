package org.reactome.web.fi.data.overlay.model;

public class OverlayProperties {

	String valueType;
	String unit;
	String sex;
	String expressionPostData;
	
	public OverlayProperties(String valueType, String unit, String sex, String expressionPostData) {
		this.valueType = valueType;
		this.unit = unit;
		this.sex = sex;
		this.expressionPostData = expressionPostData;
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

	public String getExpressionPostData() {
		return expressionPostData;
	}

	public void setExpressionPostData(String expressionPostData) {
		this.expressionPostData = expressionPostData;
	}
	
}
