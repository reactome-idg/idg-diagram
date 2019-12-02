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

	/**
	 * Can be used to direct data mediation based on data type from server
	 * @param responseText
	 * @return
	 */
	public DataOverlay transformData(String responseText) {
		OverlayEntities entities = null;
		try {
			entities = OverlayEntityDataFactory.getTargetLevelEntity(OverlayEntities.class, responseText);
		} catch (Exception e) {
			GWT.log(e.getMessage());
		}
		
		return transformExpressionEntities(entities);
		
	}

	/**
	 * Transforms TissueExpressionLevel entities into DataOverlay model
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
		if(eType == "CCLE" || eType == "GTEx" || eType == "HCA RNA" || eType == "HPM Gene" || eType == "HPM Protein")
			return getNumberValueResult(result, entities);
		else if(eType == "Cell Surface Protein Atlas" || eType == "JensenLab Knowledge UniProtKB-RC" ||eType == "JensenLab Text Mining" ||eType == "UniProt Tissue")
			return getBooleanValueResult(result, entities);
		else if(entities.getExpressionEntity().get(0).getQualValue()!=null)
			return getQualValueResult(result, entities);
			
		return result;
	}

	/**
	 * Converts OverlayEntities into DataOverlay when QualValue is present in data
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
	 * Converts Overlayentities into DataOverlay when BooleanValue is present in data
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
	 * Converts OverlayEntities into DataOverlay when NumberValue is present
	 * @param result
	 * @param entities
	 * @return
	 */
	private DataOverlay getNumberValueResult(DataOverlay result, OverlayEntities entities) {
		result.setDiscrete(false);
		
		Double minValue = null;
		Double maxValue = null;
		
		Set<String> types = new HashSet<>();
		Map<String, Double> identifierValueMap = new HashMap<>();
		for(ExpressionEntity rawEntity : entities.getExpressionEntity()) {
			if(rawEntity.getTissue() != null)
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
		result.setEType(entities.getExpressionEntity().get(0).getEtype());
		result.setTissueTypes(types.stream().sorted().collect(Collectors.toList()));
		result.setIdentifierValueMap(identifierValueMap);
		result.setMinValue(minValue);
		result.setMaxValue(maxValue);
		
		return result;
	}
}
