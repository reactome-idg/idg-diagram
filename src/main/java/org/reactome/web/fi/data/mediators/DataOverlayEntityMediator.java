package org.reactome.web.fi.data.mediators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.reactome.web.fi.data.overlay.OverlayEntities;
import org.reactome.web.fi.data.overlay.OverlayEntityDataFactory;
import org.reactome.web.fi.data.overlay.TargetLevelEntity;
import org.reactome.web.fi.model.DataOverlay;
import org.reactome.web.fi.model.DataOverlayEntity;

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
		
		//used to set max value and index value for each identifier
		List<String>discreteTypes = new ArrayList<>();
		for(TargetLevelEntity rawEntity : entities.getTargetLevelEntity()) {
			
			//add each discrete type to set
			if(!discreteTypes.contains(rawEntity.getTargetDevLevel()))
				discreteTypes.add(rawEntity.getTargetDevLevel());
			
			//add each raw entity to list of DataOverlayEntity in DataOverlay
			result.addDataOverlayEntity(new DataOverlayEntity(rawEntity.getUniprot(),
				discreteTypes.indexOf(rawEntity.getTargetDevLevel())+""));
		}
		result.setMaxValue(new Double(discreteTypes.size()));
		result.setMinValue(new Double(0));
		result.setDiscreteTypes(discreteTypes);
		return result;
	}

	private DataOverlay transformContinuous(OverlayEntities entities) {
		return null;
	}
}
