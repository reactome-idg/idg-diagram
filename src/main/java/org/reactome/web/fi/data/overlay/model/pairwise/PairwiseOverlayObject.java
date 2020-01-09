package org.reactome.web.fi.data.overlay.model.pairwise;

public class PairwiseOverlayObject {

	private String id;
	private int lineStyleIndex;
	private String lineColorHex;
	
	public PairwiseOverlayObject(String id, int lineStyleIndex, String lineColorHex) {
		this.id = id;
		this.lineStyleIndex = lineStyleIndex;
		this.lineColorHex = lineColorHex;
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

	public String getLineColorHex() {
		return lineColorHex;
	}

	public void setLineColorHex(String lineColorHex) {
		this.lineColorHex = lineColorHex;
	}
	
}
