package org.reactome.web.fi.tools.overlay.pairwise.results;

import java.util.List;

import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;

/**
 * Extends ListHandler so event can be fired after columns are sorted.
 * @author brunsont
 *
 * @param <T>
 */
public class IDGListHandler<T>  extends ListHandler<T>{
	
	public interface ColumnSortedHandler{
		void onColumnsSorted();
	}

	private ColumnSortedHandler handler;
	
	public IDGListHandler(List<T> list, ColumnSortedHandler handler) {
		super(list);
		this.handler = handler;
	}

	@Override
	public void onColumnSort(ColumnSortEvent event) {
		super.onColumnSort(event);
		handler.onColumnsSorted();
	}
	
	

}
