package org.reactome.web.fi.client.visualisers.fiview;

public class CytoscapeViewFlag {

	private static boolean showFIView = false;
	
	public static boolean isShowFIView() {
		return showFIView;
	}
	
	public static void toggleShowFIView() {
		showFIView = !showFIView;
	}
	
	public static void setShowFIView(boolean bool) {
		showFIView = bool;
	}
	
}
