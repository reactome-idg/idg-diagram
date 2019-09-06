package org.reactome.web.idg.client.flag;

public class CytoscapeViewFlag {
	private static boolean cytoscapeViewFlag = false;

	public static boolean isCytoscapeViewFlag() {
		return cytoscapeViewFlag;
	}

	public static void setCytoscapeViewFlag(boolean cytoscapeViewFlag) {
		CytoscapeViewFlag.cytoscapeViewFlag = cytoscapeViewFlag;
	}
}
