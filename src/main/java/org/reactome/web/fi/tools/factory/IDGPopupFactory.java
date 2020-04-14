package org.reactome.web.fi.tools.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.interactors.raw.RawInteractors;
import org.reactome.web.fi.data.loader.PairwiseInfoService;
import org.reactome.web.fi.data.loader.TCRDInfoLoader;
import org.reactome.web.fi.data.model.drug.Drug;
import org.reactome.web.fi.data.overlay.model.DataOverlayProperties;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseNumberEntity;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayObject;
import org.reactome.web.fi.tools.popup.IDGPopup;
import org.reactome.web.gwtCytoscapeJs.util.Console;

/**
 * 
 * @author brunsont
 *
 */
public class IDGPopupFactory{

	private static IDGPopupFactory factory;
	
	private Map<String, IDGPopup> popupMap;
	private Map<String, String> uniprotToGeneMap;
	private Set<String> tDarkSet;
	private List<PairwiseOverlayObject> currentPairwiseObjects;
	private List<PairwiseNumberEntity> pairwiseNumberEntities;
	private Collection<Drug> drugCollection;
	
	private RawInteractors rawInteractors;
	
	private int zIndexCounter = 1;
	private final int MAXIMUM_Z_INDEX = 1998;
	
	private DataOverlayProperties dataOverlayProperties;

	private Map<String, Integer> geneToTotalMap;
	
	/**
	 * On initialization, need to load TDark set and UniprotToGeneMap
	 */
	private IDGPopupFactory() {
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
				IDGPopupFactory.this.tDarkSet = tDarkSet;
			}
		});
		this.uniprotToGeneMap = PairwiseInfoService.getUniprotToGeneMap();
	}
	
	public static IDGPopupFactory get() {
		if(factory == null) {
			factory = new IDGPopupFactory();
		}
		return factory;
	}

	/**
	 * Open a popup from the diagram view
	 * @param graphObject
	 * @param string 
	 */
	public void openPopup(GraphObject graphObject, String type) {
		if(!popupMap.keySet().contains(graphObject.getStId())) {
			IDGPopup popup = new IDGPopup(graphObject, type, getZIndex());
			popupMap.put(graphObject.getStId(), popup);
			popup.show();
		}
		else
			popupMap.get(graphObject.getStId()).addType(type);
	}

	/**
	 * Open a popup from the FIView
	 * @param uniprot
	 * @param geneName
	 * @param type 
	 */
	public void openPopup(String uniprot, String geneName, String type) {
		if(!popupMap.keySet().contains(uniprot)) {
			IDGPopup popup = new IDGPopup(uniprot, geneName, type, getZIndex());
			popupMap.put(uniprot, popup);
			popup.show();
		}
		else
			popupMap.get(uniprot).addType(type);
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
		for(IDGPopup popup : popupMap.values())
			popup.updatePairwiseObjects();
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
		for(IDGPopup popup : popupMap.values())
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
		for(IDGPopup popup: popupMap.values())
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
		for(IDGPopup popup : popupMap.values())
			popup.resetZIndex();
	}
	
	public void setupNewOverlay(RawInteractors result, List<PairwiseNumberEntity> pairwiseNumberEntities, Map<String, Integer> geneToTotalMap) {
		this.setInteractorEntities(result);
		this.setPairwiseNumberEntities(pairwiseNumberEntities);
		this.setGeneToTotalMap(geneToTotalMap);
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
	
	public Collection<Drug> getDrugTargets() {
		return drugCollection;
	}

	public void setDrugTargets(Collection<Drug> uniprotToDrugTarget) {
		this.drugCollection = uniprotToDrugTarget;
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

	public void onContentRequested() {
		this.pairwiseNumberEntities = null;
		this.drugCollection = null;
		popupMap.values().forEach(x -> {
			x.setVisible(false);
		});
		popupMap.clear();
		
	}
}
