package org.reactome.web.fi.tools.overlay.pairwise.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reactome.web.diagram.data.graph.model.GraphObject;
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
public class PairwisePopupFactory{

	private static PairwisePopupFactory factory;
	
	private Map<String, PairwisePopup> popupMap;
	private Map<String, String> uniprotToGeneMap;
	private Set<String> tDarkSet;
	private List<PairwiseOverlayObject> currentPairwiseObjects;
	
	private int zIndexCounter = 1;
	private final int MAXIMUM_Z_INDEX = 1998;
	
	private DataOverlayProperties dataOverlayProperties;
	
	private PairwisePopupFactory() {
		popupMap = new HashMap<>();
		currentPairwiseObjects = new ArrayList<>();
		
		//Load uniprotToGeneMap
		PairwiseInfoService.loadUniprotToGeneMap(new AsyncCallback<Map<String, String>>() {
			@Override
			public void onFailure(Throwable caught) {
				Console.error("Uniprot to gene map failed to load.");
			}
			@Override
			public void onSuccess(Map<String, String> result) {
				PairwisePopupFactory.this.setUniprotToGeneMap(result);
				
			}
		});
		
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
	}
	
	public static PairwisePopupFactory get() {
		if(factory == null) {
			factory = new PairwisePopupFactory();
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
		return MAXIMUM_Z_INDEX;
	}

	public void resetZIndexes() {
		for(PairwisePopup popup : popupMap.values())
			popup.resetZIndex();
	}
	
	private void setUniprotToGeneMap(Map<String, String> result) {
		this.uniprotToGeneMap = result;
	}

	public Map<String, String> getUniprotToGeneMap() {
		return this.uniprotToGeneMap;
	}
	
	public Set<String> getTDarkSet(){
		return this.tDarkSet;
	}
}
