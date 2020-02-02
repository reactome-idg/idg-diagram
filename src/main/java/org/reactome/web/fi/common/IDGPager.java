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
		super.previousPage();
		handler.onPageChanged();
	}

	@Override
	public void nextPage() {
		super.nextPage();
		handler.onPageChanged();
	}

	@Override
	public void lastPage() {
		super.lastPage();
		handler.onPageChanged();
	}

	@Override
	public void firstPage() {
		super.firstPage();
		handler.onPageChanged();
	}

	@Override
	public void setPage(int index) {
		super.setPage(index);
		handler.onPageChanged();
	}
	
}
