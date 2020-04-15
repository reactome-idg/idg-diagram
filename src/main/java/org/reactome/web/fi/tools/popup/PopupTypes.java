package org.reactome.web.fi.tools.popup;

public enum PopupTypes {
	
	TR("TR"),
	DG("DG");
	
	private String textName;
	
	PopupTypes(String textName){
		this.textName = textName;
	}
	
	public String getText() {
		return this.getText();
	}
	
	public static PopupTypes fromString(String textName) {
		for(PopupTypes type : PopupTypes.values())
			if(type.textName.equalsIgnoreCase(textName))
				return type;
		
		return null;
	}
}
