package org.reactome.web.fi.data.manager;

import java.util.HashMap;
import java.util.Map;

public class StateTokenHelper {

	public StateTokenHelper() { /*Nothing Here*/}
	
	public String buildToken(Map<String, String> tokenMap) {
		StringBuilder token = new StringBuilder("/");
		int counter=0;
		for(Map.Entry<String,String> entry : tokenMap.entrySet()){
			if(entry.getKey().equals(entry.getValue())) {
				token.append(entry.getKey());
			}
			else {
				token.append(entry.getKey());
				token.append("=");
				token.append(entry.getValue());
			}
			counter++;
			if(counter < tokenMap.size()) {
				token.append("&");
			}
		}
		return token.toString();
	}
	
	public Map<String, String> buildTokenMap(String token){
		if(token.startsWith("/"))token = token.substring(1);
		
		String[] tokens = token.split("&");
		
		Map<String, String> tokenMap = new HashMap<>();
		for(String ts:tokens) {
			ts = ts.trim();
			if(ts.isEmpty())continue;
			
			String[] keyVal = ts.split("=");
			if(keyVal.length > 1)
				tokenMap.put(keyVal[0], keyVal[1]); //for key-val pair with =
			else
				tokenMap.put(keyVal[0], keyVal[0]); //key mapped to itself for singleton
		}
		return tokenMap;
	}
	
}
