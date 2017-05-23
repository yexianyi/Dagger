package net.yxy.dagger.rest;


import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

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
import net.yxy.dagger.nlp.service.NameFinderService;
import net.yxy.dagger.nlp.service.SentenceDetectorService;
import net.yxy.dagger.nlp.service.TokenizeService;
import net.yxy.dagger.util.JMSUtil;
import net.yxy.dagger.util.JSONUtil;
import opennlp.tools.util.Span;

@Path("/service/datatypes")
public class DatatypeServiceApi {
	static private Logger logger = LoggerFactory.getLogger(DatatypeServiceApi.class);  
	
	
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
				
				String[] sentences = sds.getSentences(url) ;
				for(String sentence : sentences){
					String[] tokens = ts.getTokens(sentence) ;
					Span nameSpans[] = nfs.getSpan(tokens);
					for(Span span:nameSpans){
						StringBuilder cb = new StringBuilder();  
						for (int ti = span.getStart(); ti < span.getEnd(); ti++) {
							cb.append(tokens[ti]).append(" ");
						}
						
						String dtName = cb.substring(0, cb.length() - 1) ;
						String type = span.getType() ;
						double possibility = span.getProb() ;
						
						String jsonDataType = "{"
											+ "\"DataType\":\"" + dtName+ "\"," 
											+ "\"Type\":\"" + type + "\","
											+ "\"Possibility\":\"" + possibility + "\","
											+ "\"Count\":\"" + 1 + "\""
										+ "}";
						
						if(resMap.containsKey(dtName)){
							JSONObject oldObj = resMap.get(dtName) ;
							Integer count = Integer.valueOf(JSONUtil.findAttribute(oldObj, "Count")) ;
							Double oldPoss = Double.valueOf(JSONUtil.findAttribute(oldObj, "Possibility")) ;
							if(oldPoss<possibility){
								oldObj.put("Possibility", possibility) ;
							}
							oldObj.put("Count", count+1) ;
							resMap.put(dtName, oldObj) ;
						}else{
							resMap.put(dtName, new JSONObject(jsonDataType)) ;
						}
					}
				}//end introspection
				
				//assemble results
				String jsonRsp = "{\"DataTypes\":[" ;
				
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
