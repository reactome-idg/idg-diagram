package org.reactome.web.fi.data.overlay.model.pairwise;

public class PairwiseOverlayObject {

	private String id;
	private String negativeLineColorHex;
	private String positiveLineColorHex;
	
	public PairwiseOverlayObject(String id, String negativeLineColorHex, String positiveLineColorHex) {
		this.id = id;
		this.negativeLineColorHex = negativeLineColorHex;
		this.positiveLineColorHex = positiveLineColorHex;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNegativeLineColorHex() {
		return negativeLineColorHex;
	}

	public void setNegativeLineColorHex(String negativeLineColorHex) {
		this.negativeLineColorHex = negativeLineColorHex;
	}

	public String getPositiveLineColorHex() {
		return positiveLineColorHex;
	}

	public void setPositiveLineColorHex(String positiveLineColorHex) {
		this.positiveLineColorHex = positiveLineColorHex;
	}
	
}
