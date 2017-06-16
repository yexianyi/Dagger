package net.yxy.dagger.match.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;

public class DataTypeService {
	
	private static Map<String, String> dataTypeMapping = new HashMap<String, String>() ;
	private static Map<String, String> dataTypeTransformMapping = new HashMap<String, String>() ;
	private static Map<String, Object> dataTypes = null ;
	
	static{
		initDataTypes() ;
	}
	
	private static void initDataTypes() {
		dataTypes = new HashMap<String , Object>(){{  
		     put("~any", new HashMap<String , Object>(){{
		    	 put("~number", new HashMap<String , Object>(){{
	    		 		put("~whole_number",new HashMap<String , Object>(){{
	    		 				put("@bit", "@bit") ;
	    		 				put("@integer", "@integer") ;
	    		 				put("@smallint", "@smallint") ;
	    		 				put("@tinyint", "@tinyint") ;
	    		 				put("@bigint", "@bigint") ;
	    		 		}}) ;
	    		 		put("~floating_point",new HashMap<String , Object>(){{
			 				put("@float", "@float") ;
			 				put("@real", "@real") ;
			 				put("@double", "@double") ;
			 				put("@decimal", "@decimal") ;
			 				put("@numeric", "@numeric") ;
			 		}}) ;
		    	 }});
		    	 put("~string", new HashMap<String , Object>(){{
		    		 	put("@char","@char");
		    		 	put("@varchar","@varchar");
		    		 	put("@longvarchar","@longvarchar");
		    		 	put("@nchar","@nchar");
		    		 	put("@varchar2","@varchar2");
		    		 	put("@nvarchar","@nvarchar");
		    	 }});
		    	 put("~binary", new HashMap<String , Object>(){{
		    		 	put("@binary","@binary");
		    		 	put("@varbinary","@varbinary");
		    	 }});
		    	 put("~date", new HashMap<String , Object>(){{
		    		 	put("@date","@date");
		    		 	put("@time","@time");
		    		 	put("@timestamp","@timestamp");
		    	 }});
		    	 put("~lob", new HashMap<String , Object>(){{
		    		 	put("@clob","@clob");
		    		 	put("@blob","@blob");
		    	 }});
		    	 put("@boolean", "@boolean");
		    	 put("@null", "@null");
		    	 put("~interval_year", new HashMap<String , Object>(){{
		    		 	put("@interval_year_to_month","@interval_year_to_month");
		    		 	put("@interval_year","@interval_year");
		    		 	put("@interval_month","@interval_month");
		    	 }});
		    	 put("~interval_day", new HashMap<String , Object>(){{
		    		 	put("@interval_day","@interval_day");
		    		 	put("@interval_hour","@interval_hour");
		    		 	put("@interval_minute","@interval_minute");
		    		 	put("@interval_second","@interval_second");
		    		 	put("@interval_day_to_hour","@interval_day_to_hour");
		    		 	put("@interval_day_to_minute","@interval_day_to_minute");
		    		 	put("@interval_day_to_second","@interval_day_to_second");
		    		 	put("@interval_hour_to_minute","@interval_hour_to_minute");
		    		 	put("@interval_hour_to_second","@interval_hour_to_second");
		    		 	put("@interval_minute_to_second","@interval_minute_to_second");
		    	 }});
		    	 put("@xml", "@xml");
		     }});  
		 }};  
	}
	
	public List<String> getDataTypesByTag(String tag){
		List<String> res = new ArrayList<String>() ;
		getDataTypesByTag(tag, dataTypes, res, false) ;
		return res ;
	}
	
	private void getDataTypesByTag(String tag, Map<String, Object> datatypeMap, List<String> res, boolean ifStored){
		for (String key : datatypeMap.keySet()) {  
			Object val = datatypeMap.get(key) ;
		    if(key.equalsIgnoreCase(tag)){
		    	if(val instanceof String || ifStored==true){
		    		res.add((String) val) ;
		    	}else if(val instanceof Map){
		    		Map<String, Object> subMap = (Map<String, Object>) val ;
		    		res.add((String) key) ;
		    		getDataTypesByTag(tag, subMap, res, true) ;
		    	}
		    }else{
		    	if(val instanceof Map){
		    		Map<String, Object> subMap = (Map<String, Object>) val ;
		    		if(ifStored){
		    			res.add((String) key) ;
		    			getDataTypesByTag(tag, subMap, res, true) ;
		    		}else{
		    			getDataTypesByTag(tag, subMap, res, false) ;
		    		}
		    	}else{
		    		if(ifStored){
		    			res.add((String) val) ;
		    		}
		    	}
		    } 
		  
		}  
		
		
	}
	
	
	
	
	public void addDataTypeMapping(String underlying_dt, String server_dt){
		dataTypeMapping.put(underlying_dt, server_dt) ;
	}
	

	public Map<String, String> getDataTypeMapping(){
		return dataTypeMapping ;
	}
	
	public void addDataTypeTransformMapping(String underlying_dt, String tag){
		dataTypeTransformMapping.put(underlying_dt, tag) ;
	}
	
	public Map<String, String> getDataTypeTransformMapping(){
		return dataTypeTransformMapping ;
	}
	

	public static void main(String[] args) throws JSONException {
		DataTypeService dtService = new DataTypeService() ;
		List<String> res = dtService.getDataTypesByTag("~any");
		for(String dataType : res){
			System.out.println(dataType);
		}
		
	}

}
