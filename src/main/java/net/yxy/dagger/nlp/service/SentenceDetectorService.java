package net.yxy.dagger.nlp.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import net.yxy.dagger.match.service.FunctionService;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

public class SentenceDetectorService {
	
	private InputStream modelIn ;
	private SentenceModel model ;
	
	public boolean start(){
		try {
			modelIn = new FileInputStream(SentenceDetectorService.class.getResource("/en-sent.bin").getPath());
			model = new SentenceModel(modelIn);
		} catch (IOException e) {
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
			SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
			URL target = new URL(url);
			Document doc = Jsoup.parse(target, 3 * 1000);
			String text = doc.body().text();
			return sentenceDetector.sentDetect(text);
	}

	public static void main(String[] args) throws FileNotFoundException {
		InputStream modelIn = new FileInputStream(SentenceDetectorService.class.getResource("/en-sent.bin").getPath());

		try {
			SentenceModel model = new SentenceModel(modelIn);
			SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);

//			URL url = new URL("https://www.cloudera.com/documentation/enterprise/latest/topics/impala_datatypes.html");
//			URL url = new URL("http://docs.aws.amazon.com/redshift/latest/dg/c_Supported_data_types.html");
//			URL url = new URL("https://docs.oracle.com/cd/B28359_01/server.111/b28318/datatype.htm#CNCPT1821");
//			URL url = new URL("https://docs.microsoft.com/en-us/sql/t-sql/data-types/data-types-transact-sql");
//			URL url = new URL("https://www.ibm.com/support/knowledgecenter/en/SSULQD_7.2.1/com.ibm.nz.sproc.doc/c_sproc_data_types_aliases.html?view=embed") ;
//			URL url = new URL("https://www.cloudera.com/documentation/enterprise/latest/topics/impala_math_functions.html#math_functions") ;
//			URL url = new URL("https://www.cloudera.com/documentation/enterprise/latest/topics/impala_bit_functions.html") ;
//			URL url = new URL("https://www.cloudera.com/documentation/enterprise/latest/topics/impala_conversion_functions.html") ;
//			URL url = new URL("https://www.cloudera.com/documentation/enterprise/latest/topics/impala_datetime_functions.html") ;
//			URL url = new URL("https://www.cloudera.com/documentation/enterprise/latest/topics/impala_conditional_functions.html") ;
//			URL url = new URL("https://www.cloudera.com/documentation/enterprise/latest/topics/impala_string_functions.html") ;
//			URL url = new URL("https://www.cloudera.com/documentation/enterprise/latest/topics/impala_misc_functions.html") ;
//			URL url = new URL("https://www.cloudera.com/documentation/enterprise/latest/topics/impala_aggregate_functions.html") ;
//			URL url = new URL("https://www.cloudera.com/documentation/enterprise/latest/topics/impala_analytic_functions.html") ;
//			URL url = new URL("https://www.cloudera.com/documentation/enterprise/latest/topics/impala_udf.html") ;
			URL url = new URL("https://www.cloudera.com/documentation/enterprise/latest/topics/impala_math_functions.html#math_functions") ;
			
			Document doc = Jsoup.parse(url, 3 * 1000);
			String text = doc.body().text();
//			String text = new HtmlElement(doc.body()).text();
//			String text = "IBM? PureData? System for Analytics, Version 7.2.1. Data types and aliases The following table lists the supported data types by their preferred name form, and includes supported aliases and some notes about the values. For more information about the data types and values, see the IBM Netezza Database User’s Guide. Table 1. Supported data types for variables. Data type, Alias names, Notes. BOOLEAN, BOOL, A boolean field can store true values, false values, and null. You can use the following words to specify booleans: true or false, on or off, ‘0’ or ‘1’, 'true’ or ‘false’, ‘t’ or ‘f’, ‘on’ or ‘off’, ‘yes’ or ‘no’. CHAR, CHARACTER, CHAR(n), CHARACTER(n), Fixed-length character string, blank padded to length n. If you do not specify n, the default is an unsized CHAR value. The maximum character string size is 64,000. VARCHAR CHARACTER VARYING, VARCHAR(n), CHARACTER VARYING(n), CHAR VARYING(n) Variable length character string to a maximum length of n. If you do not specify n, the default is an unsized VARCHAR value. There is no blank padding, and the value is stored as entered. The maximum character string size is 64,000.";

			String sentences[] = sentenceDetector.sentDetect(text);
			for (String sentence : sentences) {
				System.out.println(sentence);
			}
			
			
			FunctionService fs = new FunctionService() ;
			Map<String, String> standardFuncMap= fs.getStandardFunctionMap() ;
			Set<String> matchingResultSet = new HashSet<String>() ;
			
//			for(String sentence : sentences){
//				fs.matchFunction(sentence, standardFuncMap) ;
//				if(standardFuncMap.isEmpty()){
//					break ;
//				}
//			}
			
			for(String function: matchingResultSet){
				System.out.println(function);
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
