package org.reactome.web.idg.client.flag;

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
}
