package net.yxy.dagger.match.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionService {

	private static Map<String, String> standardFunctionMap = new LinkedHashMap<String, String>()  ;
	
	static{
		loadFile() ;
	}
	
	private static void loadFile(){
		BufferedReader br = null ;
		try {
			br = new BufferedReader(new FileReader(FunctionService.class.getResource("/functionEntities").getPath()));
			
			String line;
		    while ((line = br.readLine()) != null) {
		    	if(line.trim().length()>0 && !line.trim().startsWith("#")){
			    	standardFunctionMap.put(line, null) ;
			    }
		    }

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(br!=null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public Map<String, String> getStandardFunctionMap(){
        return new LinkedHashMap<String, String>(standardFunctionMap) ;
	}
	

	public boolean matchFunction(String sentence, Map<String, String> standardFuncMap) {
		for (Entry<String, String> entity : standardFuncMap.entrySet()) {
			Pattern pattern = Pattern.compile("\\b(?i)"+entity.getKey()+"\\b");
			Matcher m = pattern.matcher(sentence);
			if(m.find()){
				if(standardFuncMap.get(entity.getKey()) == null){
					standardFuncMap.put(entity.getKey(), sentence) ;
				}else{
					//ignore
				}
				return true ;
			}
		}
		
		return false ;
	}
	
	
	public static void main(String[] args){
		String str = "min_int" ;
		   Pattern r = Pattern.compile("\\bmin_int\\b", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		     Matcher m = r.matcher(str);
		System.out.println(str.matches("\\b(?i)min_int\\b"));
		
	}
	
	
}
