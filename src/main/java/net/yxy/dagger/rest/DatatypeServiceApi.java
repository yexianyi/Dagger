package net.yxy.dagger.rest;


import java.io.IOException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.yxy.dagger.global.Constants;
import net.yxy.dagger.nlp.service.NameFinderService;
import net.yxy.dagger.nlp.service.SentenceDetectorService;
import net.yxy.dagger.nlp.service.TokenizeService;
import opennlp.tools.util.Span;

@Path("/service")
public class DatatypeServiceApi {
	static private Logger logger = LoggerFactory.getLogger(DatatypeServiceApi.class);  
	
	@POST
    @Path("/datatypes/scan/{url}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response scanDatatypes(@PathParam("url") String url) {
	
		try {
			SentenceDetectorService sds = new SentenceDetectorService() ;
			TokenizeService ts = new TokenizeService() ;
			NameFinderService nfs = new NameFinderService() ;
			sds.start() ;
			ts.start() ;
			nfs.start() ;
			
			String[] sentences = sds.getSentences(url) ;
			for(String sentence : sentences){
				String[] tokens = ts.getTokens(sentence) ;
				Span nameSpans[] = nfs.getSpan(tokens);
				for(Span span:nameSpans){
					StringBuilder cb = new StringBuilder();  
					for (int ti = span.getStart(); ti < span.getEnd(); ti++) {
						cb.append(tokens[ti]).append(" ");
					}
					System.out.println(cb.substring(0, cb.length() - 1)); 
					System.out.println("\ttype: " + span.getType());  
					System.out.println("\tprob: " + span.getProb());
				}
			}
			
			nfs.close() ;
			ts.close() ;
			sds.close() ;
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Response.ResponseBuilder response = Response.ok(null).type(MediaType.APPLICATION_JSON);
		
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
