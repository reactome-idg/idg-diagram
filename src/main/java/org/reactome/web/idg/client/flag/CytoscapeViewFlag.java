package org.reactome.web.idg.client.flag;

public class CytoscapeViewFlag {
	private static boolean cytoscapeViewFlag = false;

	public static boolean isCytoscapeViewFlag() {
		return cytoscapeViewFlag;
	}

	public static void toggleCytoscapeViewFlag() {
		if (cytoscapeViewFlag == true)
			cytoscapeViewFlag = false;
		else if(cytoscapeViewFlag == false)
			cytoscapeViewFlag = true;
	}
}
