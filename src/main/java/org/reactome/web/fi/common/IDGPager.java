package org.reactome.web.fi.common;

import com.google.gwt.user.cellview.client.SimplePager;

public class IDGPager extends SimplePager{

	public interface Handler{
		void onPageChanged();
	}
	
	private Handler handler;
	
	public IDGPager(Handler handler) {
		this.handler = handler;
	}
	
	@Override
	public void previousPage() {
		// TODO Auto-generated method stub
		super.previousPage();
		
	}

	@Override
	public void nextPage() {
		// TODO Auto-generated method stub
		super.nextPage();
	}

	@Override
	public void lastPage() {
		// TODO Auto-generated method stub
		super.lastPage();
	}

	@Override
	public void firstPage() {
		// TODO Auto-generated method stub
		super.firstPage();
	}
	
}
