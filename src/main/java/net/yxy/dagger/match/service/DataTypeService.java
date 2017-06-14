package net.yxy.dagger.match.service;

import java.util.HashMap;
import java.util.Map;

public class DataTypeService {
	
	private static Map<String, String> dataTypeMapping = new HashMap<String, String>() ;
	private static Map<String, String> dataTypeTransformMapping = new HashMap<String, String>() ;
	
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
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
