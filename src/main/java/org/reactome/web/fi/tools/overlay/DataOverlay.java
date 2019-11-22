package org.reactome.web.fi.tools.overlay;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;

import java.util.ArrayList;
import java.util.List;

import org.reactome.web.fi.data.loader.TCRDInfoLoader;

public class DataOverlay  extends FlowPanel implements ClickHandler, TCRDInfoLoader.Handler{

	private List<String> expressionTypes;
	
	public DataOverlay() {
		getExpressionTypes();
		
	}

	private void getExpressionTypes() {
		TCRDInfoLoader.loadExpressionTypes(new TCRDInfoLoader.Handler() {
			
			@Override
			public void onTCRDInfoError(Throwable exception) {
				expressionTypes = new ArrayList<>();
			}
			
			@Override
			public void onExpressionTypesLoaded(List<String> info) {
				expressionTypes = info;
			}
		});
	}
	
	@Override
	public void onClick(ClickEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onExpressionTypesLoaded(List<String> info) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTCRDInfoError(Throwable exception) {
		// TODO Auto-generated method stub
		
	}

}
