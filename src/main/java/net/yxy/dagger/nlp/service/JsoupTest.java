package net.yxy.dagger.nlp.service;

import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import net.yxy.dagger.match.service.FunctionService;

public class JsoupTest {

	public static void main(String[] args) throws Exception {
		URL url = new URL("https://www.cloudera.com/documentation/enterprise/latest/topics/impala_math_functions.html#math_functions");
//		URL url = new URL("https://dev.mysql.com/doc/refman/5.7/en/numeric-functions.html");
//		URL url = new URL("http://docs.aws.amazon.com/redshift/latest/dg/r_ABS.html") ;
//		URL url = new URL("https://www.postgresql.org/docs/9.1/static/functions-math.html") ;
//		URL url = new URL("http://docs.oracle.com/cd/B19306_01/server.102/b14200/functions001.htm") ;
		
		
		Document doc = Jsoup.parse(url, 3 * 1000);

		Map<String, Integer> statistic = new HashMap<String, Integer>() ;
		
		FunctionService fs = new FunctionService() ;
		Map<String, String> standardFuncMap = fs.getStandardFunctionMap() ;
		for (Entry<String, String> entity : standardFuncMap.entrySet()) {
//			Elements elems = doc.body().getElementsContainingText(entity.getKey()) ;
			Pattern pattern = Pattern.compile("\\b(?i)"+entity.getKey()+"\\b");
			Elements elems = doc.body().getElementsMatchingOwnText(pattern) ;
			if(elems.size()>0){
				Element elem = elems.get(elems.size()-1) ;
//				System.out.println(entity.getKey()+" ---->"+elem.tagName());
				if(statistic.containsKey(elem.tagName())){
					statistic.put(elem.tagName(), statistic.get(elem.tagName())+1) ;
				}else{
					statistic.put(elem.tagName(), 1) ;
				}
			}
		}
		
		Object[] tagStatisticArray = statistic.entrySet().toArray();
		Arrays.sort(tagStatisticArray, new Comparator() {
		    public int compare(Object o1, Object o2) {
		        return ((Map.Entry<String, Integer>) o2).getValue()
		                   .compareTo(((Map.Entry<String, Integer>) o1).getValue());
		    }
		});
		
		Map<String, String> resultMap = null ;
		TokenizeService ts = new TokenizeService() ;
		ts.start() ;
		for (Object tagEntry : tagStatisticArray) {
			Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) tagEntry ;
			Elements elemCandidates = doc.body().getElementsByTag(entry.getKey()) ;//tag name
			Set<String> materialSet = new HashSet<String>() ;
			for(Element elem : elemCandidates){
				String content = elem.text() ;
				String[] tokens = ts.getTokens(content) ;
				for(String token : tokens){
					if(!materialSet.contains(token)){
						materialSet.add(token) ;
					}
				}
			}
			
			resultMap = fs.matchFunction(materialSet, standardFuncMap) ;
			
			break ;// only loop once for now 
		}
		ts.close() ;

		for (Entry<String, String> entry : resultMap.entrySet()){
			if(entry.getValue()!=null){
				System.out.println(entry.getKey()+"---->"+entry.getValue());
			}
		}
		
		

	}

}
