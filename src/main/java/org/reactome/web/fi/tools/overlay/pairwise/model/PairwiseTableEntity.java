package org.reactome.web.fi.tools.overlay.pairwise.model;

public class PairwiseTableEntity{

	private String sourceId;
	private String sourceName;
	private String sourceTDL;
	private String interactorId;
	private String interactorName;
	private String interactorTDL;
	private String dataDesc;
	private String posOrNeg;
	private String overlayValue;
	
	public PairwiseTableEntity(String sourceId, String sourceName, String interactorId, String interactorName, String dataDesc, String posOrNeg, String overlayValue) {
		this.sourceId = sourceId;
		this.sourceName = sourceName;
		this.interactorId = interactorId;
		this.interactorName = interactorName;
		this.dataDesc = dataDesc;
		this.posOrNeg = posOrNeg;
		this.overlayValue = overlayValue;
	}
	
	public PairwiseTableEntity(String sourceId, String interactorId, String posOrNeg) {
		this.sourceId = sourceId;
		this.interactorId = interactorId;
		this.posOrNeg = posOrNeg;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getInteractorId() {
		return interactorId;
	}

	public void setInteractorId(String interactorId) {
		this.interactorId = interactorId;
	}

	public String getInteractorName() {
		return interactorName;
	}

	public void setInteractorName(String interactorName) {
		this.interactorName = interactorName;
	}

	public String getDataDesc() {
		return dataDesc;
	}

	public void setDataDesc(String dataDesc) {
		this.dataDesc = dataDesc;
	}

	public String getPosOrNeg() {
		return posOrNeg;
	}

	public void setPosOrNeg(String posOrNeg) {
		this.posOrNeg = posOrNeg;
	}

	public String getOverlayValue() {
		return overlayValue;
	}

	public void setOverlayValue(String overlayValue) {
		this.overlayValue = overlayValue;
	}

	public String getInteractorTDL() {
		return interactorTDL;
	}

	public void setInteractorTDL(String targetDevelopmentLevel) {
		this.interactorTDL = targetDevelopmentLevel;
	}

	public String getSourceTDL() {
		return sourceTDL;
	}

	public void setSourceTDL(String sourceTDL) {
		this.sourceTDL = sourceTDL;
	}

	public String toStringForExport() {
		return this.sourceId + "\t" + this.sourceName + "\t" + this.sourceTDL + "\t" +
			   this.interactorId + "\t" + this.interactorName + "\t" + this.interactorTDL + "\t" +
			   this.posOrNeg + "\t" + this.dataDesc + "\t";
	}

}
