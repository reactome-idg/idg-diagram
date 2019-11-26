package org.reactome.web.fi.data.mediators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reactome.web.fi.data.overlay.model.ExpressionEntity;
import org.reactome.web.fi.data.overlay.model.OverlayEntities;
import org.reactome.web.fi.data.overlay.model.OverlayEntityDataFactory;
import org.reactome.web.fi.data.overlay.model.TargetLevelEntity;
import org.reactome.web.fi.model.DataOverlay;
import org.reactome.web.fi.model.DataOverlayEntity;
import org.reactome.web.fi.model.OverlayDataType;
import org.reactome.web.gwtCytoscapeJs.util.Console;

import com.google.gwt.core.client.GWT;

public class DataOverlayEntityMediator {

	public DataOverlay transformData(String responseText) {
		OverlayEntities entities = null;
		try {
			entities = OverlayEntityDataFactory.getTargetLevelEntity(OverlayEntities.class, responseText);
		} catch (Exception e) {
			GWT.log(e.getMessage());
		}
		if(entities.getTargetLevelEntity() != null)
			return transformTargetLevelEntities(entities);
		else if(entities.getExpressionEntity() != null)
			return transformExpressionEntities(entities);
		
		return null;
	}

	private DataOverlay transformTargetLevelEntities(OverlayEntities entities) {
		DataOverlay result = new DataOverlay();
		result.setDiscrete(true);
		result.setOverlayType(OverlayDataType.lookupType(entities.getDataType()));
		
		//used to set max value and index value for each identifier
		List<String>discreteTypes = new ArrayList<>();
		Map<String, Double> identifierValueMap = new HashMap<>();
		for(TargetLevelEntity rawEntity : entities.getTargetLevelEntity()) {
			
			//add each discrete type to set
			if(!discreteTypes.contains(rawEntity.getTargetDevLevel()))
				discreteTypes.add(rawEntity.getTargetDevLevel());
			
			//add each raw entity to list of DataOverlayEntity in DataOverlay
			result.addDataOverlayEntity(new DataOverlayEntity(rawEntity.getUniprot(),
					new Double(discreteTypes.indexOf(rawEntity.getTargetDevLevel())),
					rawEntity.getTargetDevLevel(), "non-specific"));
			identifierValueMap.put(rawEntity.getUniprot(), 
				new Double(discreteTypes.indexOf(rawEntity.getTargetDevLevel())));
		}
		result.setMaxValue(new Double(discreteTypes.size()));
		result.setMinValue(new Double(0));
		result.setLegendTypes(discreteTypes);
		result.setIdentifierValueMap(identifierValueMap);
		return result;
	}

	private DataOverlay transformExpressionEntities(OverlayEntities entities) {
		DataOverlay result = new DataOverlay();
		String eType = entities.getExpressionEntity().get(0).getEtype();
		result.setEType(entities.getExpressionEntity().get(0).getEtype());
		result.setOverlayType(OverlayDataType.lookupType(entities.getDataType()));
		
		//TODO: Make the if statements not hard coded
		if(eType == "CCLE" || eType == "GTEx" ||eType == "HCA RNA" ||eType == "HPM Gene" ||eType == "HPM Protein")
			return getNumberValueResult(result, entities);
		else if(eType == "Cell Surface Protein Atlas" || eType == "JensenLab Knowledge UniProtKB-RC" ||eType == "JensenLab Text Mining" ||eType == "UniProt Tissue")
			return getBooleanValueResult(result, entities);
		else if(eType == "Consensus" || entities.getExpressionEntity().get(0).getQualValue()!=null)
			return getQualValueResult(result, entities);
			
		return result;
	}

	private DataOverlay getQualValueResult(DataOverlay result, OverlayEntities entities) {
		result.setDiscrete(true);
		
		List<String>discreteTypes = new ArrayList<>();
		Map<String, Double> identifierValueMap = new HashMap<>();
		for(ExpressionEntity rawEntity : entities.getExpressionEntity()) {
			if(rawEntity.getQualValue() != null) {
				if(!discreteTypes.contains(rawEntity.getQualValue()))
					discreteTypes.add(rawEntity.getQualValue());
				
				result.addDataOverlayEntity(new DataOverlayEntity(rawEntity.getUniprot(), 
						new Double(discreteTypes.indexOf(rawEntity.getQualValue())), rawEntity.getEtype(), rawEntity.getTissue()));
				identifierValueMap.put(rawEntity.getUniprot(), new Double(discreteTypes.indexOf(rawEntity.getQualValue())));
			}
		}
		result.setIdentifierValueMap(identifierValueMap);
		result.setLegendTypes(discreteTypes);
		result.setMaxValue(new Double(discreteTypes.size()));
		result.setMinValue(new Double(0));
		return result;
	}

	private DataOverlay getBooleanValueResult(DataOverlay result, OverlayEntities entities) {
		result.setDiscrete(true);
		
		List<String> hit = new ArrayList<>();
		hit.add("Hit");
		result.setLegendTypes(hit);
		
		Map<String, Double> identifierValueMap = new HashMap<>();
		for(ExpressionEntity rawEntity : entities.getExpressionEntity()) {
			if(rawEntity.getBooleanValue() != null) {
				result.addDataOverlayEntity(new DataOverlayEntity(rawEntity.getUniprot(),
						new Double(0), rawEntity.getEtype(), rawEntity.getTissue()));
				identifierValueMap.put(rawEntity.getUniprot(), new Double(0));
			}
		}
		result.setIdentifierValueMap(identifierValueMap);
		
		result.setMinValue(new Double(0));
		result.setMaxValue(new Double(0));
		
		return result;
	}

	private DataOverlay getNumberValueResult(DataOverlay result, OverlayEntities entities) {
		result.setDiscrete(false);
		
		Double minValue = null;
		Double maxValue = null;
		
		List<String> types = new ArrayList<>();
		Map<String, Double> identifierValueMap = new HashMap<>();
		for(ExpressionEntity rawEntity : entities.getExpressionEntity()) {
			if(!types.contains(rawEntity.getTissue()))
				types.add(rawEntity.getTissue());
			DataOverlayEntity entity = null;
			if(rawEntity.getNumberValue() != null) {
				result.addDataOverlayEntity(entity = new DataOverlayEntity(rawEntity.getUniprot(),
						rawEntity.getNumberValue(), rawEntity.getEtype(), rawEntity.getTissue()));
				identifierValueMap.put(entity.getIdentifier(), entity.getValue());
				if(maxValue == null || entity.getValue() > maxValue)
					maxValue = entity.getValue();
				if(minValue == null || entity.getValue() < minValue)
					minValue = entity.getValue();
			}
		}
		result.setIdentifierValueMap(identifierValueMap);
		result.setMinValue(minValue);
		result.setMaxValue(maxValue);
		
		return result;
	}
}
