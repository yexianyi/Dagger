package net.yxy.dagger.match.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jettison.json.JSONException;

public class DataTypeService {
	
	private static Map<String, String> dataTypeMapping = new HashMap<String, String>() ;
	private static Map<String, String> dataTypeTransformMapping = new HashMap<String, String>() ;
	private static Map<String, Object> dataTypes = null ;
	
	static{
		initDataTypes() ;
	}
	
	private static void initDataTypes() {
		dataTypes = new LinkedHashMap<String , Object>(){{  
		     put("~any", new LinkedHashMap<String , Object>(){{
		    	 put("~number", new LinkedHashMap<String , Object>(){{
	    		 		put("~whole_number",new LinkedHashMap<String , Object>(){{
	    		 				put("@bit", "@bit") ;
	    		 				put("@integer", "@integer") ;
	    		 				put("@smallint", "@smallint") ;
	    		 				put("@tinyint", "@tinyint") ;
	    		 				put("@bigint", "@bigint") ;
	    		 		}}) ;
	    		 		put("~floating_point",new LinkedHashMap<String , Object>(){{
			 				put("@float", "@float") ;
			 				put("@real", "@real") ;
			 				put("@double", "@double") ;
			 				put("@decimal", "@decimal") ;
			 				put("@numeric", "@numeric") ;
			 		}}) ;
		    	 }});
		    	 put("~string", new LinkedHashMap<String , Object>(){{
		    		 	put("@char","@char");
		    		 	put("@varchar","@varchar");
		    		 	put("@longvarchar","@longvarchar");
		    		 	put("@nchar","@nchar");
		    		 	put("@varchar2","@varchar2");
		    		 	put("@nvarchar","@nvarchar");
		    	 }});
		    	 put("~binary", new LinkedHashMap<String , Object>(){{
		    		 	put("@binary","@binary");
		    		 	put("@varbinary","@varbinary");
		    	 }});
		    	 put("~date", new LinkedHashMap<String , Object>(){{
		    		 	put("@date","@date");
		    		 	put("@time","@time");
		    		 	put("@timestamp","@timestamp");
		    	 }});
		    	 put("~lob", new LinkedHashMap<String , Object>(){{
		    		 	put("@clob","@clob");
		    		 	put("@blob","@blob");
		    	 }});
		    	 put("@boolean", "@boolean");
		    	 put("@null", "@null");
		    	 put("~interval_year", new LinkedHashMap<String , Object>(){{
		    		 	put("@interval_year_to_month","@interval_year_to_month");
		    		 	put("@interval_year","@interval_year");
		    		 	put("@interval_month","@interval_month");
		    	 }});
		    	 put("~interval_day", new LinkedHashMap<String , Object>(){{
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
	
	public List<String> getDataTypeChildrenByTag(String tag){
		List<String> res = new ArrayList<String>() ;
		getDataTypeChildren(tag, dataTypes, res) ;
		return res ;
	}
	
	private void getDataTypeChildren(String tag, Map<String, Object> datatypeMap, List<String> res){
		for (String key : datatypeMap.keySet()) {  
			Object val = datatypeMap.get(key) ;
			//found
		    if(key.equalsIgnoreCase(tag)){
		    	if(val instanceof String){
		    		return ;
		    	}else if(val instanceof Map){
		    		Map<String, Object> subMap = (Map<String, Object>) val ;
		    		res.addAll(subMap.keySet()) ;
		    	}
		    }else{ //not found
		    	if(val instanceof Map){
		    		Map<String, Object> subMap = (Map<String, Object>) val ;
		    		getDataTypeChildren(tag, subMap, res) ;
		    	}
		    } 
		  
		}  
	}
	
	
	public List<String> getDataTypesByTag(String tag){
		List<String> res = new ArrayList<String>() ;
		getDataTypesByTag(tag, dataTypes, res, false) ;
		if(tag.startsWith("~")){
			res.remove(0) ;
		}
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
	
	
	public Object getDataTypeMapByTag(String tag){
		return getDataTypesByTag(tag, dataTypes) ;
	}
	
	private Object getDataTypesByTag(String tag, Map<String, Object> datatypeMap){
		for (String key : datatypeMap.keySet()) {  
			Object val = datatypeMap.get(key) ;
		    if(key.equalsIgnoreCase(tag)){
		    	return val ;
		    }else{
		    	if(val instanceof Map){
		    		Map<String, Object> subMap = (Map<String, Object>) val ;
		    		Object res = getDataTypesByTag(tag, subMap) ;
		    		if(res!=null){
		    			return res ;
		    		}
		    	}
		    } 
		  
		}
		
		return null;  
	}
	
	
	public Set<String> getChildDataTypeByTag(String tag){
		Set<String> set = new HashSet<String>() ;
		getChildDataTypeByTag(tag, dataTypes, set) ;
		return set ;
	}
	
	private void getChildDataTypeByTag(String tag, Map<String, Object> datatypeMap, Set<String> resultSet){
		for (String key : datatypeMap.keySet()) {  
			if(key.equalsIgnoreCase(tag)){
				if(datatypeMap.get(key) instanceof Map){
					resultSet.addAll(((Map<String, Object>)datatypeMap.get(key)).keySet()) ;
				}
				return ;
			}else{
				if(datatypeMap.get(key) instanceof Map){
					getChildDataTypeByTag(tag, (Map<String, Object>) datatypeMap.get(key), resultSet) ;
				}
			}
		  
		}
		
	}
	
	
	public String getParentDataTypeByTag(String tag){
		List<String> resultSet = new ArrayList<String>() ;
		getParentDataTypeByTag(tag, dataTypes, "~any", resultSet) ;
		return resultSet.size()>0 ? resultSet.get(0) : null ;
	}
	
	private boolean getParentDataTypeByTag(String tag, Map<String, Object> datatypeMap, String parent, List<String> resultSet){
		for (String key : datatypeMap.keySet()) {  
			if(key.equalsIgnoreCase(tag)){
				resultSet.add(parent) ;
				return true;
			}else{
				if(datatypeMap.get(key) instanceof Map){
					if(getParentDataTypeByTag(tag, (Map<String, Object>) datatypeMap.get(key), key, resultSet)){
						return true ;
					}
				}
			}
		  
		}
		
		return false ;
		
	}
	
	
	public String getDataTypeStrFrmCombinationByIdx(Map.Entry<String[], Boolean> entry, int idx){
		String[] argsList = entry.getKey() ;
		return argsList[idx] ;
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
		Object res = dtService.getDataTypeChildrenByTag("@integer");
//		String res = dtService.getParentDataTypeByTag("@integer") ;
		System.out.println(res);
		
	}

}
