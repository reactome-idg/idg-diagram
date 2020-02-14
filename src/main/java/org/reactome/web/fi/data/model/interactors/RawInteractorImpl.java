package org.reactome.web.fi.data.model.interactors;

import java.util.List;

import org.reactome.web.diagram.data.interactors.raw.RawInteractor;

public class RawInteractorImpl implements RawInteractor{

	private String acc;
	private String alias;
	private int evidences;
	private Long id;
	private double score;
	private String accURL;
	private String evidencesURL;
	private boolean isHit;
	private List<Double> exp;
	
	public RawInteractorImpl(String acc, String alias) {
		this.acc = acc;
		this.alias = alias;
	}
	
	@Override
	public String getAcc() {
		return acc;
	}

	@Override
	public String getAlias() {
		return alias;
	}

	@Override
	public Integer getEvidences() {
		return evidences;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public double getScore() {
		return score;
	}

	@Override
	public String getAccURL() {
		return accURL;
	}

	@Override
	public String getEvidencesURL() {
		return evidencesURL;
	}

	@Override
	public void setIsHit(Boolean isHit) {
		this.isHit = isHit;
	}

	@Override
	public Boolean getIsHit() {
		return isHit;
	}

	@Override
	public void setExp(List<Double> exp) {
		this.exp = exp;
	}

	@Override
	public List<Double> getExp() {
		return exp;
	}

}
