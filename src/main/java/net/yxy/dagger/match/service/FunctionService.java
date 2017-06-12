package net.yxy.dagger.match.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

public class FunctionService {

	private static Properties standardFunctionSet ;
	
	static{
		standardFunctionSet = new Properties() ;
		InputStream in = FunctionService.class.getResourceAsStream("/functionEntities");
		try {
			standardFunctionSet.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public Map<String, String> getStandardFunctionSet(){
		Map<String, String> map = new HashMap<String, String> () ;
		Enumeration en = standardFunctionSet.propertyNames();
        while (en.hasMoreElements()) {
         String key = (String) en.nextElement();
               String Property = standardFunctionSet.getProperty (key);
               System.out.println(key+Property);
               map.put(key+Property, Property);
        }
        
        return map ;
	}
	

	public void matchFunction(String sentence, Map<String, String> standardFuncMap, Set<String> matchingResultSet) {
		for (Entry<String, String> entity : standardFuncMap.entrySet()) {
			if(sentence.matches("/b"+entity.getKey()+"/b")){
				matchingResultSet.add(entity.getKey()) ;
				standardFuncMap.remove(entity.getKey()) ;
				break ;
			}
		}
		
		
	}
	
	
	
	public static void main(String[] args){
		Enumeration en = standardFunctionSet.propertyNames();
        while (en.hasMoreElements()) {
         String key = (String) en.nextElement();
               String Property = standardFunctionSet.getProperty (key);
           }
	}
	
	
}
