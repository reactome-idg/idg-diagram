package org.reactome.web.fi.data.model;

/**
 * 
 * @author brunsont
 *
 */
public class PathwayEnrichmentResult {

	private String stId;
	private String name;
	private Double fdr;
	private Double pVal;
	
	public PathwayEnrichmentResult(String stId, String name, Double fdr, Double pVal) {
		super();
		this.stId = stId;
		this.name = name;
		this.fdr = fdr;
		this.pVal = pVal;
	}
	public String getStId() {
		return stId;
	}
	public void setStId(String stId) {
		this.stId = stId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getFdr() {
		return fdr;
	}
	public void setFdr(Double fdr) {
		this.fdr = fdr;
	}
	public Double getpVal() {
		return pVal;
	}
	public void setpVal(Double pVal) {
		this.pVal = pVal;
	}
	
}
