//package net.yxy.dagger.nlp.service;
//
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URL;
//
//import javax.ws.rs.core.Response;
//
//import org.codehaus.jettison.json.JSONException;
//import org.codehaus.jettison.json.JSONObject;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//
//import net.yxy.dagger.util.JSONUtil;
//import opennlp.tools.namefind.NameFinderME;
//import opennlp.tools.namefind.TokenNameFinderModel;
//import opennlp.tools.sentdetect.SentenceDetectorME;
//import opennlp.tools.sentdetect.SentenceModel;
//import opennlp.tools.tokenize.Tokenizer;
//import opennlp.tools.tokenize.TokenizerME;
//import opennlp.tools.tokenize.TokenizerModel;
//import opennlp.tools.util.Span;
//
//public class DatatypeServiceImpl {
//
//	
//	public Response getDatatypes(String jsonReq) throws FileNotFoundException, JSONException {
//		InputStream sentModelIn = new FileInputStream(DatatypeServiceImpl.class.getResource("/en-sent.bin").getPath());
//		InputStream tokenModelIn = new FileInputStream(DatatypeServiceImpl.class.getResource("/en-token.bin").getPath());
//		InputStream nameModelIn = new FileInputStream(DatatypeServiceImpl.class.getResource("/en-ner-datatype.bin").getPath());
//
//		try {
//			SentenceModel sentModel = new SentenceModel(sentModelIn);
//			SentenceDetectorME sentenceDetector = new SentenceDetectorME(sentModel);
//			
//			TokenizerModel tokenModel = new TokenizerModel(tokenModelIn);
//			Tokenizer tokenizer = new TokenizerME(tokenModel);
//			
//			TokenNameFinderModel nameFinderModel = new TokenNameFinderModel(nameModelIn);
//			NameFinderME nameFinder = new NameFinderME(nameFinderModel);
//			
//			JSONObject jsonObj = new JSONObject(jsonReq) ;
//			String urlStr = JSONUtil.findAttribute(jsonObj, "url") ;
//			
//			URL url = new URL(urlStr);
//			
//			Document doc = Jsoup.parse(url, 60000);
//			String text = doc.body().text();
//			String sentences[] = sentenceDetector.sentDetect(text);
//			
//			for(String sentence : sentences){
//				String[] tokens = tokenizer.tokenize(sentence) ;
//				Span nameSpans[] = nameFinder.find(tokens);
//				for(Span span:nameSpans){
//					StringBuilder cb = new StringBuilder();  
//					for (int ti = span.getStart(); ti < span.getEnd(); ti++) {
//						cb.append(tokens[ti]).append(" ");
//					}
//					System.out.println(cb.substring(0, cb.length() - 1)); 
//					System.out.println("\ttype: " + span.getType());  
//					System.out.println("\tprob: " + span.getProb());
//
//				}
//			}
//			
//		}
//		finally {
//		  if (sentModelIn != null) {
//		    try {
//		    	sentModelIn.close();
//		    }
//		    catch (IOException e) {
//		    }
//		  }
//		  
//		  if (tokenModelIn != null) {
//			    try {
//			    	tokenModelIn.close();
//			    }
//			    catch (IOException e) {
//			    }
//		  }
//		  
//		  if (nameModelIn != null) {
//			    try {
//			    	nameModelIn.close();
//			    }
//			    catch (IOException e) {
//			    }
//		  }
//		}
//			
//	}
//
//	
//}
