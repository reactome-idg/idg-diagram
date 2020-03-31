package org.reactome.web.fi.tools.overlay.pairwise.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.interactors.raw.RawInteractors;
import org.reactome.web.fi.data.loader.PairwiseInfoService;
import org.reactome.web.fi.data.loader.TCRDInfoLoader;
import org.reactome.web.fi.data.overlay.model.DataOverlayProperties;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseNumberEntity;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayObject;
import org.reactome.web.fi.tools.overlay.pairwise.PairwisePopup;
import org.reactome.web.gwtCytoscapeJs.util.Console;


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
	private List<PairwiseNumberEntity> pairwiseNumberEntities;
	private RawInteractors rawInteractors;
	
	private int zIndexCounter = 1;
	private final int MAXIMUM_Z_INDEX = 1998;
	
	private DataOverlayProperties dataOverlayProperties;

	private Map<String, Integer> geneToTotalMap;
	
	/**
	 * On initialization, need to load TDark set and UniprotToGeneMap
	 */
	private PairwiseOverlayFactory() {
		popupMap = new HashMap<>();
		currentPairwiseObjects = new ArrayList<>();
		
		//load Dark Protein list
		TCRDInfoLoader.loadTDarkSet(new TCRDInfoLoader.TDarkHandler() {
			@Override
			public void onTDarkLoadedError(Throwable exception) {
				Console.error(exception);
			}
			
			@Override
			public void onTDarkLoaded(Set<String> tDarkSet) {
				PairwiseOverlayFactory.this.tDarkSet = tDarkSet;
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
	
	public int getNumberOfPopups() {
		return popupMap.keySet().size()+1;
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
		if(this.uniprotToGeneMap == null)
			uniprotToGeneMap = PairwiseInfoService.getUniprotToGeneMap();
		return this.uniprotToGeneMap;
	}
	
	public Set<String> getTDarkSet(){
		return this.tDarkSet;
	}
	
	public void setInteractorEntities(RawInteractors result) {
		this.rawInteractors = result;
	}
	
	public RawInteractors getRawInteractors() {
		return this.rawInteractors;
	}

	public void setPairwiseNumberEntities(List<PairwiseNumberEntity> entities) {
		this.pairwiseNumberEntities = entities;
	}
	
	public Map<String, Integer> getPairwiseCountForUniprot(String uniprot){
		Map<String, Integer> result = new HashMap<>();
		
		if(pairwiseNumberEntities == null || pairwiseNumberEntities.size() == 0)
			return result;
		
		for(PairwiseNumberEntity entity : pairwiseNumberEntities) {
			if(entity.getGene() == uniprot) {
				result.put(entity.getDataDesc().getId(), entity.getPosNum()+entity.getNegNum());
			}
		}
		
		return result;
	}
	
	public List<PairwiseNumberEntity> getPairwiseNumberEntities(){
		return this.pairwiseNumberEntities;
	}

	public boolean hasOverlay() {
		if(this.currentPairwiseObjects != null && this.currentPairwiseObjects.size() > 0) return true;
		return false;
	}

	public void setGeneToTotalMap(Map<String, Integer> geneToTotalMap) {
		this.geneToTotalMap = geneToTotalMap;
	}
	
	public Map<String, Integer> getGeneToTotalMap(){
		return this.geneToTotalMap;
	}
}
