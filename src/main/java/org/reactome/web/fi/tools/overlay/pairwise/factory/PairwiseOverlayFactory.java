package org.reactome.web.fi.tools.overlay.pairwise.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.interactors.raw.RawInteractorEntity;
import org.reactome.web.diagram.data.interactors.raw.RawInteractors;
import org.reactome.web.fi.data.loader.PairwiseInfoService;
import org.reactome.web.fi.data.loader.TCRDInfoLoader;
import org.reactome.web.fi.data.overlay.model.DataOverlayProperties;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayObject;
import org.reactome.web.fi.tools.overlay.pairwise.PairwisePopup;
import org.reactome.web.gwtCytoscapeJs.util.Console;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author brunsont
 *
 */
public class PairwiseOverlayFactory{

	private static PairwiseOverlayFactory factory;
	
	private Map<String, PairwisePopup> popupMap;
	private Map<String, String> uniprotToGeneMap;
	private Set<String> tDarkSet;
	private List<PairwiseOverlayObject> currentPairwiseObjects;
	private RawInteractors rawInteractors;
	
	private int zIndexCounter = 1;
	private final int MAXIMUM_Z_INDEX = 1998;
	
	private DataOverlayProperties dataOverlayProperties;
	
	/**
	 * On initialization, need to load TDark set and UniprotToGeneMap
	 */
	private PairwiseOverlayFactory() {
		popupMap = new HashMap<>();
		currentPairwiseObjects = new ArrayList<>();
		
		//load Dark Protein list
		TCRDInfoLoader.loadTDarkSet(new AsyncCallback<Set<String>>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
			@Override
			public void onSuccess(Set<String> result) {
				tDarkSet = result;
			}
		});
		this.uniprotToGeneMap = PairwiseInfoService.getUniprotToGeneMap();
	}
	
	public static PairwiseOverlayFactory get() {
		if(factory == null) {
			factory = new PairwiseOverlayFactory();
		}
		return factory;
	}

	/**
	 * Open a popup from the diagram view
	 * @param graphObject
	 */
	public void openPopup(GraphObject graphObject) {
		if(!popupMap.keySet().contains(graphObject.getStId()) && currentPairwiseObjects.size() > 0) {
			PairwisePopup popup = new PairwisePopup(graphObject, currentPairwiseObjects, getZIndex());
			popupMap.put(graphObject.getStId(), popup);
			popup.show();
		}
	}

	/**
	 * Open a popup from the FIView
	 * @param uniprot
	 * @param geneName
	 */
	public void openPopup(String uniprot, String geneName) {
		if(!popupMap.keySet().contains(uniprot) && currentPairwiseObjects.size() > 0) {
			PairwisePopup popup = new PairwisePopup(uniprot, geneName, currentPairwiseObjects, getZIndex());
			popupMap.put(uniprot, popup);
			popup.show();
		}
	}
	
	/**
	 * Provide the right z index for popups.
	 * @return
	 */
	private int getZIndex() {
		int result = 100 + zIndexCounter;
		zIndexCounter += 2;
		return result;
	}
	
	/**
	 * Removes node from map on close
	 * @param id
	 */
	public void removePopup(String id) {
		popupMap.remove(id);
	}
	
	/**
	 * sets properties of current Pairwise overlay options
	 * @param pairwiseOverlayObjects
	 */
	public void setCurrentPairwiseProperties(List<PairwiseOverlayObject> pairwiseOverlayObjects) {
		this.currentPairwiseObjects = pairwiseOverlayObjects;
		for(PairwisePopup popup : popupMap.values())
			popup.updatePairwiseObjects(this.currentPairwiseObjects);
	}
	
	public List<PairwiseOverlayObject> getCurrentPairwiseProperties(){
		return this.currentPairwiseObjects;
	}

	/**
	 * sets current data overlay properties and causes popups to reload overlay data with the new properties.
	 * @param dataOverlayProperties
	 */
	public void setDataOverlayProperties(DataOverlayProperties dataOverlayProperties) {
		this.dataOverlayProperties = dataOverlayProperties;
		for(PairwisePopup popup : popupMap.values())
			popup.loadOverlay();
	}

	public DataOverlayProperties getDataOverlayProperties() {
		return this.dataOverlayProperties;
	}

	/**
	 * Directs open popups to change overlay column on column changed
	 * @param column
	 */
	public void setOverlayColumn(int column) {
		for(PairwisePopup popup: popupMap.values())
			popup.changeOverlayColumn(column);
	}

	/**
	 * Instructs all currently open panels to reset to their original z index and returns value of max z index for panel to be focused on.
	 * @return
	 */
	public int getMaxZIndex() {
		return MAXIMUM_Z_INDEX;
	}

	public void resetZIndexes() {
		for(PairwisePopup popup : popupMap.values())
			popup.resetZIndex();
	}

	public Map<String, String> getUniprotToGeneMap() {
		return this.uniprotToGeneMap;
	}
	
	public Set<String> getTDarkSet(){
		return this.tDarkSet;
	}
	
	public void setInteractorEntities(RawInteractors result) {
		this.rawInteractors = result;
	}
	
	public int getInteractorCountForUniprot(String uniprot) {
		List<RawInteractorEntity> interactorEntities = rawInteractors.getEntities();
		if(interactorEntities == null || interactorEntities.size() == 0) return 0;
		for(RawInteractorEntity entity : interactorEntities) {
			if(entity.getAcc() == uniprot)
				return entity.getCount();
		}
		return 0;
	}
	
	public RawInteractors getRawInteractors() {
		return this.rawInteractors;
	}
}
