package org.reactome.web.fi.overlay.analysis;

import java.util.List;

import org.reactome.web.analysis.client.model.ExpressionSummary;

public class ExpressionSummaryImpl implements ExpressionSummary {

	private List<String> columnNames;
	private Double min;
	private double max;
	
	public ExpressionSummaryImpl(List<String> columnNames, Double min, Double max) {
		this.columnNames = columnNames;
		this.min = min;
		this.max = max;
	}
	
	@Override
	public List<String> getColumnNames() {
		return this.columnNames;
	}

	@Override
	public Double getMin() {
		return min;
	}

	@Override
	public Double getMax() {
		return max;
	}

}
