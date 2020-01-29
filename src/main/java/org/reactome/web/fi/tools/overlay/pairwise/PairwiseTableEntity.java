package org.reactome.web.fi.tools.overlay.pairwise;

public class PairwiseTableEntity {

	private String diagramSource;
	private String pairwiseInteractor;
	private String interactionSource;
	private String posOrNeg;
	private String data;
	
	public PairwiseTableEntity(String diagramSource, String pairwiseInteractor, String interactionSource, String posOrNeg, String data) {
		this.diagramSource = diagramSource;
		this.pairwiseInteractor = pairwiseInteractor;
		this.interactionSource = interactionSource;
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

	public String getInteractionSource() {
		return interactionSource;
	}

	public void setInteractionSource(String identifier) {
		this.interactionSource = identifier;
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
