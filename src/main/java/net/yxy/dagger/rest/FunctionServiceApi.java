package net.yxy.dagger.rest;


import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

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
			int counter = 1 ; 
			
			FunctionService funcService = new FunctionService() ;
			//Get each of function mapping from template one by one
			JSONArray funcArray = funcsObj.getJSONArray("functions") ;
			for(Map.Entry<String, String> funcMappingItem : funcService.getAllFuncSignatureMap().entrySet()) {
				travelFuncMappingItem(funcMappingItem, funcArray, counter) ;
			}
			
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		response = Response.ok(funcsObj.toString()).type(MediaType.APPLICATION_JSON) ;
		
		
		///////////////HTTP Cache by Duration////////////////
		CacheControl cc = new CacheControl() ;
		cc.setMaxAge(Constants.REFRESH_INTERVAL);
		response.cacheControl(cc) ;
		
		
		return response.build();
	}
	
	private void travelFuncMappingItem(Entry<String, String> funcMappingItem, JSONArray funcArray, int counter) {
		String funcFullName = funcMappingItem.getKey() ;
		FunctionService funcService = new FunctionService() ;
		String funcName = funcService.getFuncNameFrmSignature(funcFullName) ;
		
		//travel child param combination
		Map<String[], Boolean> combMap = funcService.getFuncParamCombinates(funcFullName) ;
		if(combMap.size()==1){
			combMap.forEach((k,v)->{
				String itemFullName = funcName + k.toString();
				if(itemFullName.replaceAll(" ","").equalsIgnoreCase(funcFullName.replaceAll(" ", ""))){ //current node is already leaf node
					return ;
				}
			});
		}else{
			
		}
		for(Entry<String[], Boolean> paramComb : combMap.entrySet()) {
			travelFuncMappingItem(funcMappingItem, funcArray, counter) ;
		}
		
		JSONObject funcObj = new JSONObject() ;
//		funcObj.put("id", counter++) ;
//		funcObj.put("name", funcMappingItem.getKey()) ;
//		funcObj.put("result", "") ;
//		funcObj.put("children", new JSONArray()) ;
	}
	
	@Deprecated
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
			Map<String[], Boolean> resultMap = funcService.testFunction(entry.getKey(), entry.getValue()) ;
			funcService.consolidateResults(resultMap);
			resultMap.forEach((k,v)->{
				if(v==Boolean.TRUE){
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
				}
			});
		}
		
		
		
		response = Response.ok(funcsObj.toString()).type(MediaType.APPLICATION_JSON) ;
		
		CacheControl cc = new CacheControl() ;
		cc.setMaxAge(Constants.REFRESH_INTERVAL);
		response.cacheControl(cc) ;
		
		
		
		return response.build();
	}
	
	
	/**
	 * 
	 * @param jdbcName
	 * @param jdbcClass
	 * @param jdbcUrl
	 * @param jdbcUserName
	 * @param jdbcPassword
	 * @param funcFullName
	 * @return
	 * {
		   "result":{
		      "success":"true",
		      "error":"none"
		   }
		}
	 */
	@POST
    @Path("/test")
    @Produces(MediaType.APPLICATION_JSON)
	public Response testFunction(	@FormDataParam("jdbcName") String jdbcName, 
									@FormDataParam("jdbcCls") String jdbcClass, 
									@FormDataParam("jdbcUrl") String jdbcUrl,
									@FormDataParam("jdbcUserName") String jdbcUserName,
									@FormDataParam("jdbcPwd") String jdbcPassword,
									@FormDataParam("fullName") String funcFullName) {
		Response.ResponseBuilder response = null ;
		JSONObject funcsObj = new JSONObject() ;
		try {
			funcsObj.put("result", new JSONObject()) ;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		String stortedPath = getStorePath(jdbcName) ;
		JdbcDriverService jdbcService = new JdbcDriverService(stortedPath, jdbcClass, jdbcUrl, jdbcUserName, jdbcPassword) ;
		FunctionService funcService = new FunctionService(jdbcService) ;
//			String funcName = funcService.getFuncNameFrmSignature(funcFullName) ;
			String funcSqlStr = funcService.getFuncSqlStr(funcFullName) ;
			Map<String[], Boolean> resultMap = funcService.testFunction(funcFullName, funcSqlStr) ;
			funcService.consolidateResults(resultMap);
			resultMap.forEach((k,v)->{
				if(v==Boolean.TRUE){
//					System.out.print(funcName+"(") ;
//					for(int i=0; i<k.length; i++){
//						System.out.print(k[i]);
//						if(i==k.length-1){
//							System.out.print(")") ;
//							System.out.println() ;
//							return ;
//						}
//						System.out.print(",") ;
//					}
//					System.out.println() ;
				}
			});
		
		
		
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
