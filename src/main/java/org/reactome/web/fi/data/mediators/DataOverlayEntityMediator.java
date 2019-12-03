package org.reactome.web.fi.data.mediators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

	private String returnValueType;
	
	/**
	 * Can be used to direct data mediation based on data type from server
	 * @param responseText
	 * @return
	 */
	public DataOverlay transformData(String responseText, String returnValueType) {
		this.returnValueType = returnValueType;
		OverlayEntities entities = null;
		try {
			entities = OverlayEntityDataFactory.getTargetLevelEntity(OverlayEntities.class, responseText);
		} catch (Exception e) {
			GWT.log(e.getMessage());
		}
		
		return transformExpressionEntities(entities);
		
	}

	/**
	 * Transforms TissueExpressionLevel entities into DataOverlayPanel model
	 * @param entities
	 * @return
	 */
	private DataOverlay transformExpressionEntities(OverlayEntities entities) {
		DataOverlay result = new DataOverlay();
		if(entities.getExpressionEntity().size() == 0)
			return result;
		String eType = entities.getExpressionEntity().get(0).getEtype();
		result.setOverlayType(OverlayDataType.lookupType(entities.getDataType()));
		
		//TODO: Make the if statements not hard coded
		if(returnValueType == "Number")
			return getNumberValueResult(result, entities);
		else if(returnValueType == "Boolean")
			return getBooleanValueResult(result, entities);
		else if(returnValueType == "String")
			for(ExpressionEntity entity : entities.getExpressionEntity()) {
				if(entity.getQualValue() != null)
					return getQualValueResult(result, entities);
				else if(entity.getStringValue() != null)
					return getStringValueResult(result, entities);
			}
			
		return result;
	}

	private DataOverlay getStringValueResult(DataOverlay result, OverlayEntities entities) {
		// TODO Auto-generated method stub
		return new DataOverlay();
	}

	/**
	 * Converts OverlayEntities into DataOverlayPanel when QualValue is present in data
	 * @param result
	 * @param entities
	 * @return
	 */
	private DataOverlay getQualValueResult(DataOverlay result, OverlayEntities entities) {
		result.setDiscrete(true);
		
		List<String>discreteTypes = new ArrayList<>();
		Set<String>tissues = new HashSet<>();
		Map<String, Double> identifierValueMap = new HashMap<>();
		for(ExpressionEntity rawEntity : entities.getExpressionEntity()) {
			if(rawEntity.getQualValue() != null) {
				if(!discreteTypes.contains(rawEntity.getQualValue()))
					discreteTypes.add(rawEntity.getQualValue());
				
				if(rawEntity.getTissue() != null)
					tissues.add(rawEntity.getTissue());
				
				result.addDataOverlayEntity(new DataOverlayEntity(rawEntity.getUniprot(), 
						new Double(discreteTypes.indexOf(rawEntity.getQualValue())), rawEntity.getEtype(), rawEntity.getTissue()));
				identifierValueMap.put(rawEntity.getUniprot(), new Double(discreteTypes.indexOf(rawEntity.getQualValue())));
			}
		}
		result.setIdentifierValueMap(identifierValueMap);
		result.setEType(entities.getExpressionEntity().get(0).getEtype());
		result.setTissueTypes(tissues.stream().sorted().collect(Collectors.toList()));
		result.setLegendTypes(discreteTypes);
		result.setMaxValue(new Double(discreteTypes.size()));
		result.setMinValue(new Double(0));
		
		//set etype for target dev level, which has no associated eType
		if(result.getEType() == null) {
			result.setEType("Target Development Level");
		}
		
		return result;
	}

	/**
	 * Converts Overlayentities into DataOverlayPanel when BooleanValue is present in data
	 * @param result
	 * @param entities
	 * @return
	 */
	private DataOverlay getBooleanValueResult(DataOverlay result, OverlayEntities entities) {
		result.setDiscrete(true);
		
		Map<String, Double> identifierValueMap = new HashMap<>();
		Set<String> tissues = new HashSet<>();
		for(ExpressionEntity rawEntity : entities.getExpressionEntity()) {
			if(rawEntity.getTissue() != null)
				tissues.add(rawEntity.getTissue());
			
			if(rawEntity.getBooleanValue() != null) {
				result.addDataOverlayEntity(new DataOverlayEntity(rawEntity.getUniprot(),
						new Double(0), rawEntity.getEtype(), rawEntity.getTissue()));
				identifierValueMap.put(rawEntity.getUniprot(), new Double(0));
			}
		}
		
		List<String> hit = new ArrayList<>();
		if(identifierValueMap.size() > 0) {
			hit.add("Hit");
			result.setLegendTypes(hit);
		}
			
		result.setIdentifierValueMap(identifierValueMap);
		result.setTissueTypes(tissues.stream().sorted().collect(Collectors.toList()));
		result.setEType(entities.getExpressionEntity().get(0).getEtype());
		result.setMinValue(new Double(0));
		result.setMaxValue(new Double(0));
		
		return result;
	}
	
	/*
	 * Converts OverlayEntities into DataOverlayPanel when NumberValue is present
	 * @param result
	 * @param entities
	 * @return
	 */
	private DataOverlay getNumberValueResult(DataOverlay result, OverlayEntities entities) {
		result.setDiscrete(false);
		
		Double minValue = Double.MAX_VALUE;
		Double maxValue = Double.MIN_VALUE;
		
		Set<String> types = new HashSet<>();
		Map<String, Double> identifierValueMap = new HashMap<>();
		Map<String, List<DataOverlayEntity>> uniprotToEntitiesMap = new HashMap<>();
		for(ExpressionEntity rawEntity : entities.getExpressionEntity()) {
			if(rawEntity.getTissue() != null)
				types.add(rawEntity.getTissue());
			DataOverlayEntity entity = null;
			if(rawEntity.getNumberValue() != null) {
				result.addDataOverlayEntity(entity = new DataOverlayEntity(rawEntity.getUniprot(),
						rawEntity.getNumberValue(), rawEntity.getEtype(), rawEntity.getTissue()));
				identifierValueMap.put(entity.getIdentifier(), entity.getValue());
				
				//testing new way to organize 
				if(!uniprotToEntitiesMap.containsKey(rawEntity.getUniprot()))
					uniprotToEntitiesMap.put(rawEntity.getUniprot(), new ArrayList<>());	
				uniprotToEntitiesMap.get(rawEntity.getUniprot()).add(entity);
				
				
				if(entity.getValue() > maxValue)
					maxValue = entity.getValue();
				if(entity.getValue() < minValue)
					minValue = entity.getValue();
			}
		}
		result.setEType(entities.getExpressionEntity().get(0).getEtype());
		result.setTissueTypes(types.stream().sorted().collect(Collectors.toList()));
		result.setIdentifierValueMap(identifierValueMap);
		result.setUniprotToEntitiesMap(uniprotToEntitiesMap);
		if(minValue != Double.MAX_VALUE)
			result.setMinValue(minValue);
		if(maxValue != Double.MIN_VALUE)
			result.setMaxValue(maxValue);
		
		return result;
	}
}
