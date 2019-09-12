package org.reactome.web.fi.events;

import org.reactome.web.fi.handlers.NodeClickedHandler;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author brunsont
 *
 */
public class NodeClickedEvent extends GwtEvent<NodeClickedHandler>{
    public static Type<NodeClickedHandler> TYPE = new Type<>();

    private String nodeId;
    private String shortName;

    public NodeClickedEvent(String nodeId, String shortName) {
        this.nodeId = nodeId;
        this.shortName = shortName;
    }
    
	@Override
	public Type<NodeClickedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(NodeClickedHandler handler) {
		handler.onNodeClicked(this);
		
	}
	
	public String getNodeId() {
		return nodeId;
	}
	
	public String getShortName() {
		return shortName;
	}
	
    @Override
    public String toString() {
        return "NodeClickedEvent{" +
                "content=" + getNodeId() + "}";
    }
}
