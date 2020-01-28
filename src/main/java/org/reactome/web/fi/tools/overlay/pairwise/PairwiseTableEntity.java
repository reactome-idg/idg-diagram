package org.reactome.web.fi.tools.overlay.pairwise;

public class PairwiseTableEntity {

	private String diagramSource;
	private String pairwiseInteractor;
	private String identifier;
	private String posOrNeg;
	private String data;
	
	public PairwiseTableEntity(String diagramSource, String pairwiseInteractor, String identifier, String posOrNeg, String data) {
		this.diagramSource = diagramSource;
		this.pairwiseInteractor = pairwiseInteractor;
		this.identifier = identifier;
		this.posOrNeg = posOrNeg;
		this.data = data;
	}

	public String getDiagramSource() {
		return diagramSource;
	}

	public void setDiagramSource(String diagramSource) {
		this.diagramSource = diagramSource;
	}

	public String getPairwiseInteractor() {
		return pairwiseInteractor;
	}

	public void setPairwiseInteractor(String pairwiseInteractor) {
		this.pairwiseInteractor = pairwiseInteractor;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
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
