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
		
		if(entities.getDiscrete() == "true")
			return transformDiscrete(entities);
		else if(entities.getDiscrete() == "false")
			return transformContinuous(entities);
		
		return null;
	}

	private DataOverlay transformDiscrete(OverlayEntities entities) {
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
				new Double(discreteTypes.indexOf(rawEntity.getTargetDevLevel()))));
			identifierValueMap.put(rawEntity.getUniprot(), 
				new Double(discreteTypes.indexOf(rawEntity.getTargetDevLevel())));
		}
		result.setMaxValue(new Double(discreteTypes.size()));
		result.setMinValue(new Double(0));
		result.setDiscreteTypes(discreteTypes);
		result.setIdentifierValueMap(identifierValueMap);
		return result;
	}

	private DataOverlay transformContinuous(OverlayEntities entities) {
		DataOverlay result = new DataOverlay();
		result.setDiscrete(false);
		
		Double minValue = null;
		Double maxValue = null;
		for(ExpressionEntity rawEntity : entities.getExpressionEntity()) {
			//reset min and max if needed
			if(rawEntity.getNumberValue()==null)
				continue;
			
			if(maxValue == null || rawEntity.getNumberValue() > maxValue)
				maxValue = rawEntity.getNumberValue();
			if(minValue == null || rawEntity.getNumberValue() < minValue)
				minValue = rawEntity.getNumberValue();
			
			//add rawEntity to list of DataOverlayEntities
			if(rawEntity.getNumberValue() != null)
				result.addDataOverlayEntity(new DataOverlayEntity(rawEntity.getUniprot(),
					rawEntity.getNumberValue()));
		}
		result.setMinValue(minValue);
		result.setMaxValue(maxValue);
			
		return result;
	}
}
