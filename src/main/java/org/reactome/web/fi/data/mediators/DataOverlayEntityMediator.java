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
		for(ExpressionEntity rawEntity : entities.getExpressionEntity()) {
			if(rawEntity.getNumberValue()==null)
				continue;
	
			if(!types.contains(rawEntity.getTissue()))
				types.add(rawEntity.getTissue());
			
			//add rawEntity to list of DataOverlayEntities
			DataOverlayEntity entity = null;
			if(rawEntity.getNumberValue() != null)
				result.addDataOverlayEntity(entity = new DataOverlayEntity(rawEntity.getUniprot(),
					rawEntity.getNumberValue(), rawEntity.getEtype()));
			else if(rawEntity.getBooleanValue() != null) {
				Double booleanVal = (double) 0;
				if(rawEntity.getBooleanValue() == true) booleanVal = (double) 1;
				result.addDataOverlayEntity(entity = new DataOverlayEntity(rawEntity.getUniprot(),
						booleanVal, rawEntity.getEtype()));
			}
			else if(rawEntity.getQualValue() != null) {
				//implement for qualValue
			}
			
			if(maxValue == null || entity.getValue() > maxValue)
				maxValue = entity.getValue();
			if(minValue == null || entity.getValue() < minValue)
				minValue = entity.getValue();
			
		}
		result.setMinValue(minValue);
		result.setMaxValue(maxValue);
			
		return result;
	}
}
