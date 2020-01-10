package org.reactome.web.fi.data.overlay.model.pairwise;

public class PairwiseOverlayObject {

	private String id;
	private int lineStyleIndex;
	private String negativeLineColorHex;
	private String positiveLineColorHex;
	
	public PairwiseOverlayObject(String id, int lineStyleIndex, String negativeLineColorHex, String positiveLineColorHex) {
		this.id = id;
		this.lineStyleIndex = lineStyleIndex;
		this.negativeLineColorHex = negativeLineColorHex;
		this.positiveLineColorHex = positiveLineColorHex;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getLineStyleIndex() {
		return lineStyleIndex;
	}

	public void setLineStyleIndex(int lineStyleIndex) {
		this.lineStyleIndex = lineStyleIndex;
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
