package org.reactome.web.fi.tools.overlay.pairwise;

public class PairwiseTableEntity {

	private String sourceId;
	private String sourceName;
	private String interactorId;
	private String interactorName;
	private String dataDesc;
	private String posOrNeg;
	private String data;
	
	public PairwiseTableEntity(String sourceId, String sourceName, String interactorId, String interactorName, String dataDesc, String posOrNeg, String data) {
		this.sourceId = sourceId;
		this.sourceName = sourceName;
		this.interactorId = interactorId;
		this.interactorName = interactorName;
		this.dataDesc = dataDesc;
		this.posOrNeg = posOrNeg;
		this.data = data;
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

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
