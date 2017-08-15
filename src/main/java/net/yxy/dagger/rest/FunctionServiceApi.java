package net.yxy.dagger.rest;


import java.io.File;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.yxy.dagger.core.service.JdbcDriverService;
import net.yxy.dagger.global.Constants;
import net.yxy.dagger.match.service.FunctionService;

@Path("/service/functions")
public class FunctionServiceApi {
	static private Logger logger = LoggerFactory.getLogger(FunctionServiceApi.class);  
	
	@GET
    @Path("/getAll")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getAllFuncMappings() {
		Response.ResponseBuilder response = null ;
		JSONObject funcsObj = new JSONObject() ;	
		try {
			funcsObj.put("functions", new JSONArray()) ;
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		int counter = 1 ; 
		
		FunctionService funcService = new FunctionService() ;
		for(Map.Entry<String, String> entry : funcService.getAllFuncSignatureMap().entrySet()) {  
			String funcName = funcService.getFuncNameFrmSignature(entry.getKey()) ;
			try {
				JSONObject funcItem = null ;
				if(funcsObj.has("functions")){
					JSONArray funcArray = funcsObj.getJSONArray("functions") ;
					//find specific func item in array
					for(int i=0; i<funcArray.length(); i++){
						if(funcArray.getJSONObject(i).getString("name").equals(funcName)){
							funcItem = funcArray.getJSONObject(i) ;
						}
					}
				}
					
				//Not found -> create & store new item
				if(funcItem==null){
					funcItem = new JSONObject() ;
					funcItem.put("id", counter++) ;
					funcItem.put("name", funcName) ;
					funcItem.put("result", "");
					funcItem.put("children", new JSONArray()) ;
					funcsObj.accumulate("functions", funcItem) ;
				} 
				
				//add func def str to children
				JSONObject funcChild = new JSONObject() ;
				funcChild.put("id", counter++) ;
				funcChild.put("name", entry.getKey()) ;
				funcChild.put("result", "");
				funcChild.put("children", new JSONArray()) ;
				funcItem.accumulate("children", funcChild) ;
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		response = Response.ok(funcsObj.toString()).type(MediaType.APPLICATION_JSON) ;
		
		///////////////HTTP Cache by Expire////////////////
		//Expires:
		//The Expires response header indicates the amount of time that the response entity should be cached. 
		//It is useful to set the expiration for data that is not going to change for a known time length. 
		//Browsers use this response header to manage their caches among other user agents.
		//The javax.ws.rs.core.Response.ResponseBuilder#expires() method can be used to set the Expires header.
//		Date expirationDate = new Date(System.currentTimeMillis() + 1000*60);
//		response.expires(expirationDate);
		
		///////////////HTTP Cache by Duration////////////////
		CacheControl cc = new CacheControl() ;
		cc.setMaxAge(Constants.REFRESH_INTERVAL);
		response.cacheControl(cc) ;
		
		
		///////////////HTTP Cache by E-tag////////////////

		
		//		logger.debug("Checking if there an Etag and whether there is a change in the order...");
//		EntityTag etag = computeEtagForOrder(list);
//		Response.ResponseBuilder responseBuilder = request.evaluatePreconditions(etag);
//		if (responseBuilder != null) {
//			// Etag match
//			list.debug("Order has not changed..returning unmodified response code");
//			return responseBuilder.build();
//		}
//		list.debug("Returning full Order to the Client");
//		OrderDto orderDto = (OrderDto) beanMapper.map(order, OrderDto.class);
//		responseBuilder = Response.ok(orderDto).tag(etag);
//		return responseBuilder.build();
//		
		
		return response.build();
	}
	
	
	
	
	@POST
    @Path("/testAll")
    @Produces(MediaType.APPLICATION_JSON)
	public Response testAll(	@FormDataParam("jdbcName") String jdbcName, 
								@FormDataParam("jdbcCls") String jdbcClass, 
								@FormDataParam("jdbcUrl") String jdbcUrl,
								@FormDataParam("jdbcUserName") String jdbcUserName,
								@FormDataParam("jdbcPwd") String jdbcPassword) {
		Response.ResponseBuilder response = null ;
		JSONObject funcsObj = new JSONObject() ;
//		funcsObj.put("results", value) ;
		
		String stortedPath = getStorePath(jdbcName) ;
		JdbcDriverService jdbcService = new JdbcDriverService(stortedPath, jdbcClass, jdbcUrl, jdbcUserName, jdbcPassword) ;
		FunctionService funcService = new FunctionService(jdbcService) ;
		
		for(Map.Entry<String, String> entry : funcService.getAllFuncSignatureMap().entrySet()) {  
			String funcName = funcService.getFuncNameFrmSignature(entry.getKey()) ;
			System.out.println("Testing "+ funcName);
			Map<String[], Boolean> resultMap = funcService.testFunction(entry.getKey(), entry.getValue()) ;
			funcService.consolidateResults(resultMap);
			resultMap.forEach((k,v)->{
				System.out.print(funcName+"(") ;
				for(int i=0; i<k.length; i++){
					System.out.print(k[i]);
					if(i==k.length-1){
						System.out.print(")") ;
						System.out.println() ;
						return ;
					}
					System.out.print(",") ;
				}
				System.out.println() ;
			});
		}
		
		
		
		response = Response.ok(funcsObj.toString()).type(MediaType.APPLICATION_JSON) ;
		
		CacheControl cc = new CacheControl() ;
		cc.setMaxAge(Constants.REFRESH_INTERVAL);
		response.cacheControl(cc) ;
		
		
		
		return response.build();
	}

	
	private String getStorePath(String jdbcName) {
		String path = Constants.UPLOAD_FOLDER+ File.separator + jdbcName ;
		File dir = new File(path) ;
		return Boolean.TRUE == dir.exists() ? path : null;
	}
	
	public static void main(String[] args){
		FunctionServiceApi api = new FunctionServiceApi() ;
		api.getAllFuncMappings() ;
	}

}
