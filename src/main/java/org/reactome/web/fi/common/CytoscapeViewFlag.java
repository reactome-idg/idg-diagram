package org.reactome.web.fi.common;

/**
 * 
 * @author brunsont
 *
 */
public class CytoscapeViewFlag {
	private static boolean cytoscapeViewFlag = false;
	
	public CytoscapeViewFlag() { /*Nothing Here */ }

	public static boolean isCytoscapeViewFlag() {
		return cytoscapeViewFlag;
	}

	public static void toggleCytoscapeViewFlag() {
		if (cytoscapeViewFlag == true)
			cytoscapeViewFlag = false;
		else
			cytoscapeViewFlag = true;
	}
	
	public static void ensureCytoscapeViewFlagFalse() {
		cytoscapeViewFlag = false;
	}
}
