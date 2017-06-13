package net.yxy.dagger.rest;


import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.yxy.dagger.global.Constants;
import net.yxy.dagger.match.service.FunctionService;
import net.yxy.dagger.nlp.service.NameFinderService;
import net.yxy.dagger.nlp.service.SentenceDetectorService;
import net.yxy.dagger.nlp.service.TokenizeService;
import net.yxy.dagger.util.JSONUtil;

@Path("/service/functions")
public class FunctionServiceApi {
	static private Logger logger = LoggerFactory.getLogger(FunctionServiceApi.class);  
	
	
	@POST
    @Path("/scan")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public Response scanDatatypes(String jsonReq) {
		Response.ResponseBuilder response = null ;
		Map<String, JSONObject> resMap = new HashMap<String, JSONObject>() ;
		try {
			SentenceDetectorService sds = new SentenceDetectorService() ;
			TokenizeService ts = new TokenizeService() ;
			NameFinderService nfs = new NameFinderService() ;
			
			if(sds.start() && ts.start() && nfs.start()){
				JSONObject jsonObj = new JSONObject(jsonReq) ;
				String url = JSONUtil.findAttribute(jsonObj, "url") ;
				
				FunctionService fs = new FunctionService() ;
				Map<String, String> standardFuncMap= fs.getStandardFunctionMap() ;
				Set<String> matchingResultSet = new HashSet<String>() ;
				
				String[] sentences = sds.getSentences(url) ;
				for(String sentence : sentences){
					fs.matchFunction(sentence, standardFuncMap) ;
					if(standardFuncMap.isEmpty()){
						break ;
					}
				}
				
				//assemble results
				String jsonRsp = "{\"Functions\":[" ;
				
				Iterator<Map.Entry<String, JSONObject>> entries = resMap.entrySet().iterator();  
				while (entries.hasNext()) {  
					Entry<String, JSONObject> entry = entries.next();  
					jsonObj = entry.getValue() ;
					jsonRsp += jsonObj.toString() ;
					if(entries.hasNext()){
						jsonRsp += "," ;
					}
				}  
				
				jsonRsp += "]}";
				response = Response.ok(jsonRsp).type(MediaType.APPLICATION_JSON) ;
			}
			
			if(	nfs.close() && ts.close() && sds.close()){
				
			}
			
			
		} catch (IOException | JSONException e) {
			e.printStackTrace();
			response = Response.serverError().type(MediaType.APPLICATION_JSON);
		} 
		
		
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


}
