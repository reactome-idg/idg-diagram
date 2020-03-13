package org.reactome.web.fi.data.loader;

import java.util.*;

import org.reactome.web.fi.data.model.TDarkProteinSet;
import org.reactome.web.fi.data.model.TDarkProteinSetFactory;
import org.reactome.web.fi.data.overlay.model.ExpressionTypeEntities;
import org.reactome.web.fi.data.overlay.model.ExpressionTypeFactory;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author brunsont
 *
 */
public class TCRDInfoLoader{

	public interface ETypeHandler{
		void onExpressionTypesLoaded(ExpressionTypeEntities entities);
		void onExpressionTypesLoadedError(Throwable exception);
	}
	public interface TissueHandler{
		void onTissueTypesLoaded(List<String> tissuesList);
		void onTissueTypesLoadedError(Throwable exception);
	}
	public interface TDarkHandler{
		void onTDarkLoaded(Set<String> tDarkSet);
		void onTDarkLoadedError(Throwable exception);
	}
	
	private static final String BASE_URL = "/tcrdws/";
	
	public static void loadExpressionTypes(ETypeHandler handler) {
		String url = BASE_URL + "expressionTypes";
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
		requestBuilder.setHeader("Accept", "application/json");
		try {
			requestBuilder.sendRequest(null, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					if(response.getStatusCode() == Response.SC_OK) {
						JSONValue val = JSONParser.parseStrict(response.getText());
						JSONObject obj = new JSONObject();
						obj.put("expressionTypeEntity", val);
						try {
							ExpressionTypeEntities entities = ExpressionTypeFactory.getExpressionTypeEntities(ExpressionTypeEntities.class, obj.toString());
							handler.onExpressionTypesLoaded(entities);
						} catch (Exception e) {
							handler.onExpressionTypesLoadedError(e);
						}
					}
				}
				@Override
				public void onError(Request request, Throwable exception) {
					handler.onExpressionTypesLoadedError(new Exception(exception.getMessage()));
				}
			});
		} catch(RequestException ex) {
			handler.onExpressionTypesLoadedError(ex);
		}
	}
	
	/**
	 * Loads set of T Dark proteins and returns in a set
	 * @param callback
	 */
	public static void loadTDarkSet(TDarkHandler callback) {
		String url = BASE_URL + "tdark/uniprots";
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
		requestBuilder.setHeader("Accept", "application/json");
		try {
			requestBuilder.sendRequest(null, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					if(response.getStatusCode() == Response.SC_OK) {
						try {
							JSONObject obj = new JSONObject();
							obj.put("proteins", JSONParser.parseStrict(response.getText()));
							TDarkProteinSet tDarkProteins = TDarkProteinSetFactory.getSetEntity(TDarkProteinSet.class, obj.toString());
							Set<String> proteins = new HashSet<>();
							proteins.addAll(tDarkProteins.getProteins());
							callback.onTDarkLoaded(proteins);
						} catch (Exception e) {
							callback.onTDarkLoadedError(e);
						}
					}
				}
				@Override
				public void onError(Request request, Throwable exception) {
					callback.onTDarkLoadedError(exception);
				}
			});
		}catch(RequestException ex) {
			callback.onTDarkLoadedError(ex);
		}
	}

	/**
	 * Loads a list of tissues available for a given eType
	 * @param eType
	 * @param handler
	 */
	public static void loadTissueTypes(String eType, TissueHandler handler) {
		String fixedEType = eType.replaceAll(" ", "+");
		String url = BASE_URL + "tissues/" + fixedEType;
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
		requestBuilder.setHeader("Accept", "application/json");
		try {
			requestBuilder.sendRequest(null, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					List<String> tissueList = new ArrayList<>();
					if(response.getStatusCode() == Response.SC_OK) {
						JSONArray array = JSONParser.parseStrict(response.getText()).isArray();
						if(array != null) {
							for(int i=0; i<array.size(); i++) {
								tissueList.add(array.get(i).isString().stringValue());
							}
						}
					}
					handler.onTissueTypesLoaded(tissueList);
				}
				@Override
				public void onError(Request request, Throwable exception) {
					handler.onTissueTypesLoadedError(new Exception(exception.getMessage()));	
				}
			});
		} catch(RequestException ex) {
			handler.onTissueTypesLoadedError(ex);
		}
	}
	
	/**
	 * Loads a single target development level for a passed in uniprot and returns to callback
	 * @param uniprot
	 * @param callback
	 */
	public static void loadSingleTargetLevelProtein(String uniprot, AsyncCallback<String> callback) {
		String url = "/tcrdws/targetlevel/uniprot/" + uniprot;
		
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
		requestBuilder.setHeader("Accept", "application/json");
		try {
			requestBuilder.sendRequest(null, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					if(response.getStatusCode() == Response.SC_OK) {
						JSONValue val = JSONParser.parseStrict(response.getText());
						callback.onSuccess(val.isObject().get("targetDevLevel").isString().stringValue());
					}
				}
				@Override
				public void onError(Request request, Throwable exception) {
					callback.onFailure(exception);
				}
			});
		} catch (RequestException ex) {
			callback.onFailure(ex);
		}
	}
}
