package org.reactome.web.fi.data.overlay.model;

public class DataOverlayProperties {

	private String valueType;
	private String unit;
	private String sex;
	private String tissues;
	private String eType;
	private String uniprots;
	private String pathwayStableId;
	
	public DataOverlayProperties(String valueType, String unit, String sex, String tissues, String eType) {
		this.valueType = valueType;
		this.unit = unit;
		this.sex = sex;
		this.tissues = tissues;
		this.eType = eType;
	}

	public String getTissues() {
		return tissues;
	}

	public String getPathwayStableId() {
		return this.pathwayStableId;
	}
	
	public void setPathwayStableId(String pathwayStableId) {
		this.pathwayStableId = pathwayStableId;
	}
	
	public void setTissues(String tissues) {
		this.tissues = tissues;
	}


	public String geteType() {
		return eType;
	}


	public void seteType(String eType) {
		this.eType = eType;
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
	
	public String getUniprots() {
		return uniprots;
	}
	
	public void setUniprots(String uniprots) {
		this.uniprots = uniprots;
	}
	
	@Override
	public int hashCode() {
		return (valueType + unit + sex + eType + tissues).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null || getClass() != obj.getClass()) return false;
		DataOverlayProperties prop = (DataOverlayProperties) obj;
		return valueType == prop.getValueType() &&
			   unit == prop.getUnit() &&
			   sex == prop.getSex() &&
			   tissues == prop.getTissues() &&
			   eType == prop.geteType();
	}
}
