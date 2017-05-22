package net.yxy.dagger.nlp.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class NameFinderService {
	
	private InputStream nameModelIn = null ;
	private TokenNameFinderModel nameFinderModel = null ;
	
	public boolean start() {
		try {
			nameModelIn = new FileInputStream(NameFinderService.class.getResource("/en-ner-datatype.bin").getPath());
			nameFinderModel = new TokenNameFinderModel(nameModelIn) ;
		} catch (IOException e) {
			e.printStackTrace();
			return false ;
		}
		
		return true ;
	}
	
	public boolean close(){
		 if (nameModelIn != null) {
		    try {
		    	nameModelIn.close();
		    }
		    catch (IOException e) {
		    	e.printStackTrace();
		    	return false ;
		    }
		  }
		 return true ;
	}
	
	public Span[] getSpan(String[] tokens) throws IOException{
		
		NameFinderME nameFinder = new NameFinderME(nameFinderModel);
		return nameFinder.find(tokens) ;
	}

	
	public static void main(String[] args) throws FileNotFoundException {
		InputStream sentModelIn = new FileInputStream(NameFinderService.class.getResource("/en-sent.bin").getPath());
		InputStream tokenModelIn = new FileInputStream(TokenizeService.class.getResource("/en-token.bin").getPath());
		InputStream nameModelIn = new FileInputStream(NameFinderService.class.getResource("/en-ner-datatype.bin").getPath());

		try {
			SentenceModel sentModel = new SentenceModel(sentModelIn);
			SentenceDetectorME sentenceDetector = new SentenceDetectorME(sentModel);
			
			TokenizerModel tokenModel = new TokenizerModel(tokenModelIn);
			Tokenizer tokenizer = new TokenizerME(tokenModel);
			
			TokenNameFinderModel nameFinderModel = new TokenNameFinderModel(nameModelIn);
			NameFinderME nameFinder = new NameFinderME(nameFinderModel);

			URL url = new URL("https://www.cloudera.com/documentation/enterprise/latest/topics/impala_datatypes.html");
//			URL url = new URL("https://gpdb.docs.pivotal.io/500Alpha/ref_guide/data_types.html");
//			URL url = new URL("https://docs.oracle.com/cd/B28359_01/server.111/b28318/datatype.htm#CNCPT1821");
//			URL url = new URL("https://dev.mysql.com/doc/refman/5.7/en/integer-types.html");
//			URL url = new URL("https://www.postgresql.org/docs/9.2/static/datatype.html");
//			URL url = new URL("https://docs.microsoft.com/en-us/sql/t-sql/data-types/data-types-transact-sql");
//			URL url = new URL("https://www.ibm.com/support/knowledgecenter/en/SSULQD_7.2.1/com.ibm.nz.sproc.doc/c_sproc_data_types_aliases.html?view=embed");
			
			Document doc = Jsoup.parse(url, 60000);
			String text = doc.body().text();
			String sentences[] = sentenceDetector.sentDetect(text);
			
			for(String sentence : sentences){
				String[] tokens = tokenizer.tokenize(sentence) ;
				Span nameSpans[] = nameFinder.find(tokens);
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
			
		}
		catch (IOException e) {
		  e.printStackTrace();
		}
		finally {
		  if (sentModelIn != null) {
		    try {
		    	sentModelIn.close();
		    }
		    catch (IOException e) {
		    }
		  }
		  
		  if (tokenModelIn != null) {
			    try {
			    	tokenModelIn.close();
			    }
			    catch (IOException e) {
			    }
		  }
		  
		  if (nameModelIn != null) {
			    try {
			    	nameModelIn.close();
			    }
			    catch (IOException e) {
			    }
		  }
		}
			

	}

}
