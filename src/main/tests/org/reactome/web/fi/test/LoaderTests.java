package org.reactome.web.fi.test;

import org.junit.Test;
import org.reactome.web.fi.data.loader.TCRDLoader;

public class LoaderTests implements TCRDLoader.Handler{
	
	@Test
	public void testTCRDLoader() throws Exception {
		TCRDLoader loader = new TCRDLoader(this);
		loader.loadTargetLevels("P00533,P10721");
	}

	@Override
	public void onTargetLevelLoaded(String json) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTargetLevelLoadedError(Throwable exception) {
		// TODO Auto-generated method stub
		
	}
	
}
