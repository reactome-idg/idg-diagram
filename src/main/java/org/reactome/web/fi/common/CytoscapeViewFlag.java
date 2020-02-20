package org.reactome.web.fi.common;

import com.google.gwt.user.client.History;

/**
 * 
 * @author brunsont
 *
 */
public class CytoscapeViewFlag {
	private static boolean cytoscapeViewFlag = false;
	private static final String FI = "FI";
	private static final String DIAGRAM = "DIAGRAM";
	
	public CytoscapeViewFlag() { /*Nothing Here */ }

	public static boolean isCytoscapeViewFlag() {
		return cytoscapeViewFlag;
	}

	public static void toggleCytoscapeViewFlag() {
		cytoscapeViewFlag = !cytoscapeViewFlag;
		if (cytoscapeViewFlag == false) {
			History.newItem("VIZ=" + DIAGRAM);
		}
		else {
			History.newItem("VIZ=" + FI);
		}
	}
	
	public static void ensureCytoscapeViewFlagFalse() {
		cytoscapeViewFlag = false;
	}
}
