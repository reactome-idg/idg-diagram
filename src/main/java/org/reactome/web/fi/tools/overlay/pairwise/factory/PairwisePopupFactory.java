package org.reactome.web.fi.tools.overlay.pairwise.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.fi.data.overlay.model.DataOverlayProperties;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayObject;
import org.reactome.web.fi.tools.overlay.pairwise.PairwisePopup;

/**
 * 
 * @author brunsont
 *
 */
public class PairwisePopupFactory{

	private static PairwisePopupFactory factory;
	
	private Map<String, PairwisePopup> popupMap;
	private List<PairwiseOverlayObject> currentPairwiseObjects;
	
	private int zIndexCounter = 1;
	private final int MAXIMUM_Z_INDEX = 1999;
	
	private DataOverlayProperties dataOverlayProperties;
	
	private PairwisePopupFactory() {
		popupMap = new HashMap<>();
		currentPairwiseObjects = new ArrayList<>();
	}
	
	public static PairwisePopupFactory get() {
		if(factory == null)
			factory = new PairwisePopupFactory();
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
	 * Provide the right z index for popups.
	 * @return
	 */
	private int getZIndex() {
		int result = 100 + zIndexCounter;
		zIndexCounter += 2;
		return result;
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
	
	public void removePopup(String id) {
		popupMap.remove(id);
	}
	
	public void setCurrentPairwiseProperties(List<PairwiseOverlayObject> pairwiseOverlayObjects) {
		this.currentPairwiseObjects = pairwiseOverlayObjects;
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

	public void setOverlayColumn(int column) {
		for(PairwisePopup popup: popupMap.values())
			popup.changeOverlayColumn(column);
	}

	/**
	 * Instructs all currently open panels to reset to their original z index and returns value of max z index for panel to be focused on.
	 * @return
	 */
	public int getMaxZIndex() {
		for(PairwisePopup popup : popupMap.values())
			popup.resetZIndex();
		return MAXIMUM_Z_INDEX;
	}
}
