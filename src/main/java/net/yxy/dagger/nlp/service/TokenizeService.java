package net.yxy.dagger.nlp.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class TokenizeService {
	
	private InputStream tokenModelIn ;
	
	public boolean start(){
		try {
			tokenModelIn = new FileInputStream(TokenizeService.class.getResource("/en-token.bin").getPath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false ;
		}
		
		return true;
	}
	
	public boolean close(){
		if (tokenModelIn != null) {
			try {
				tokenModelIn.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true ;
	}
	
	public String[] getTokens(String sentence) throws IOException {
			TokenizerModel tokenModel = new TokenizerModel(tokenModelIn);
			Tokenizer tokenizer = new TokenizerME(tokenModel);
			return tokenizer.tokenize(sentence) ;
	}

	public static void main(String[] args) throws FileNotFoundException {
		InputStream sentModelIn = new FileInputStream(TokenizeService.class.getResource("/en-sent.bin").getPath());
		InputStream tokenModelIn = new FileInputStream(TokenizeService.class.getResource("/en-token.bin").getPath());

		try {
			
			SentenceModel sentModel = new SentenceModel(sentModelIn);
			SentenceDetectorME sentenceDetector = new SentenceDetectorME(sentModel);
			
			TokenizerModel tokenModel = new TokenizerModel(tokenModelIn);
			Tokenizer tokenizer = new TokenizerME(tokenModel);

			URL url = new URL("https://www.cloudera.com/documentation/enterprise/latest/topics/impala_datatypes.html");
			Document doc = Jsoup.parse(url, 3 * 1000);
			String text = doc.body().text();
			String sentences[] = sentenceDetector.sentDetect(text);
			for (String sentence : sentences) {
				String tokens[] = tokenizer.tokenize(sentence);
				for(String token:tokens){
					System.out.println(token);
				}
				System.out.println("----------------------") ;
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
		}
			

	}

}
