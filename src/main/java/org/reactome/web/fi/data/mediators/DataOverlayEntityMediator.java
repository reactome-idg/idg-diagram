package org.reactome.web.fi.data.mediators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reactome.web.fi.data.overlay.ExpressionEntity;
import org.reactome.web.fi.data.overlay.OverlayEntities;
import org.reactome.web.fi.data.overlay.OverlayEntityDataFactory;
import org.reactome.web.fi.data.overlay.TargetLevelEntity;
import org.reactome.web.fi.model.DataOverlay;
import org.reactome.web.fi.model.DataOverlayEntity;
import org.reactome.web.fi.model.OverlayDataType;

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
				rawEntity.getTargetDevLevel()));
			identifierValueMap.put(rawEntity.getUniprot(), 
				new Double(discreteTypes.indexOf(rawEntity.getTargetDevLevel())));
		}
		result.setMaxValue(new Double(discreteTypes.size()));
		result.setMinValue(new Double(0));
		result.setTypes(discreteTypes);
		result.setIdentifierValueMap(identifierValueMap);
		return result;
	}

	private DataOverlay transformExpressionEntities(OverlayEntities entities) {
		DataOverlay result = new DataOverlay();
		result.setEType(entities.getExpressionEntity().get(0).getEtype());
		result.setDiscrete(false);
		
		Double minValue = null;
		Double maxValue = null;
		
		List<String> types = new ArrayList<>();
		Map<String, Double> identifierValueMap = new HashMap<>();
		for(ExpressionEntity rawEntity : entities.getExpressionEntity()) {
	
			if(!types.contains(rawEntity.getTissue()))
				types.add(rawEntity.getTissue());
			
			//add rawEntity to list of DataOverlayEntities
			DataOverlayEntity entity = null;
			if(rawEntity.getNumberValue() != null)
				result.addDataOverlayEntity(entity = new DataOverlayEntity(rawEntity.getUniprot(),
					rawEntity.getNumberValue(), rawEntity.getEtype()));
			else if(rawEntity.getBooleanValue() != null) {
				result.setDiscrete(true);
				List<String> hit = new ArrayList<String>();
				hit.add("hit");
				result.setTypes(hit);
				result.addDataOverlayEntity(entity = new DataOverlayEntity(rawEntity.getUniprot(),
						new Double(0), rawEntity.getEtype()));
				identifierValueMap.put(rawEntity.getUniprot(), new Double(0));
			}
			else if(rawEntity.getQualValue() != null) {
				//implement for qualValue
			}
			
			if(maxValue == null || entity.getValue() > maxValue)
				maxValue = entity.getValue();
			if(minValue == null || entity.getValue() < minValue)
				minValue = entity.getValue();
			
		}
		result.setIdentifierValueMap(identifierValueMap);
		result.setMinValue(minValue);
		result.setMaxValue(maxValue);
			
		return result;
	}
}
