package net.yxy.dagger.nlp.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

public class SentenceDetectorService {
	
	private InputStream modelIn ;
	
	public boolean start(){
		try {
			modelIn = new FileInputStream(SentenceDetectorService.class.getResource("/en-sent.bin").getPath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false ;
		}
		return true ;
	}
	
	public boolean close(){
		  if (modelIn != null) {
		      try {
				modelIn.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false ;
			}
		  }
		  return true ;
	}
	
	public String[] getSentences(String url) throws IOException{
		

		try {
			SentenceModel model = new SentenceModel(modelIn);
			SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);

			URL target = new URL(url);
			Document doc = Jsoup.parse(target, 3 * 1000);
			String text = doc.body().text();

			return sentenceDetector.sentDetect(text);
		}
		finally {
		
		}
		
	}

	public static void main(String[] args) throws FileNotFoundException {
		InputStream modelIn = new FileInputStream(SentenceDetectorService.class.getResource("/en-sent.bin").getPath());

		try {
			SentenceModel model = new SentenceModel(modelIn);
			SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);

//			URL url = new URL("https://www.cloudera.com/documentation/enterprise/latest/topics/impala_datatypes.html");
//			URL url = new URL("http://docs.aws.amazon.com/redshift/latest/dg/c_Supported_data_types.html");
//			URL url = new URL("https://docs.oracle.com/cd/B28359_01/server.111/b28318/datatype.htm#CNCPT1821");
			URL url = new URL("https://docs.microsoft.com/en-us/sql/t-sql/data-types/data-types-transact-sql");
			
			Document doc = Jsoup.parse(url, 3 * 1000);
			String text = doc.body().text();

			String sentences[] = sentenceDetector.sentDetect(text);
			for (String sentence : sentences) {
				System.out.println(sentence);
			}
		}
		catch (IOException e) {
		  e.printStackTrace();
		}
		finally {
		  if (modelIn != null) {
		    try {
		      modelIn.close();
		    }
		    catch (IOException e) {
		    }
		  }
		}
			

	}

}
