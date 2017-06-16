package net.yxy.dagger.match.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionService {

	private static Map<String, String> standardFunctionMap = new LinkedHashMap<String, String>()  ;
	private static Map<String, String> funcMappings = new LinkedHashMap<String, String>()  ;
	
	
	static{
		loadFile(standardFunctionMap, "/functionEntities") ;
		loadFile(funcMappings, "/FunctionMappings") ;
	}
	
	private static void loadFile(Map<String, String> storeMap, String file_path){
		BufferedReader br = null ;
		try {
			br = new BufferedReader(new FileReader(FunctionService.class.getResource(file_path).getPath()));
			
			String line;
		    while ((line = br.readLine()) != null) {
		    	if(line.trim().length()>0 && !line.trim().startsWith("#")){
		    		String[] array = line.trim().split(":") ;
		    		if(array.length>1){
		    			storeMap.put(array[0], array[1]) ;
		    		}else{
		    			storeMap.put(line, null) ;
		    		}
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
//        return new LinkedHashMap<String, String>(standardFunctionMap) ;
        return standardFunctionMap ;
	}
	

	public Set<String> matchFunction(Set<String> materialSet, Map<String, String> standardFuncMap) {
		Set<String> resultSet = new LinkedHashSet<String>() ;
		
		for(Entry<String, String> entity : standardFuncMap.entrySet()) {
			Pattern pattern = Pattern.compile("\\b(?i)"+entity.getKey()+"\\b");
			Iterator<String> it = materialSet.iterator();
			while(it.hasNext()){
			    String content = it.next();
			    Matcher m = pattern.matcher(content);
				while(m.find()){
					resultSet.add(content) ;
				}
			}
			
		}
		
		return resultSet ;
	}
	
	private Connection getConnection(String dbUrl, String dbName, String userName, String password) throws SQLException {

	    Connection conn = null;
	    Properties connectionProps = new Properties();
	    connectionProps.put("user",userName);
	    connectionProps.put("password", password);

        conn = DriverManager.getConnection(dbUrl, connectionProps);
	    System.out.println("Connected to database");
	    return conn;
	}
	
	public void CreateTestSchema(){
		String sql = "create table alldatatypes(\n" + 
				"    BIGINT_COL BIGINT,\n" + 
				"    BOOLEAN_COL BOOLEAN,\n" + 
				"    CHAR_COL CHAR(1),\n" + 
				"    DECIMA_COL DECIMAL,\n" + 
				"    DOUBLE_COL DOUBLE,\n" + 
				"    FLOAT_COL FLOAT,\n" + 
				"    INT_COL INT,\n" + 
				"    REAL_COL REAL,\n" + 
				"    SMALLINT_COL SMALLINT,\n" + 
				"    STRING_COL STRING,\n" + 
				"    TIMESTAMP_COL TIMESTAMP,\n" + 
				"    TINYINT_COL TINYINT,\n" + 
				"    VARCHAR_COL VARCHAR\n" + 
				")" ;
	}
	
	public Map<String, String> testFunction(String funcDefStr, String funcSqlStr){
		//funcDefStr =  CORR(~number,~string) ;
		Pattern pattern = Pattern.compile("\\(.*?\\)");
		Matcher m = pattern.matcher(funcDefStr);
		if(m.find()){
			String paramStr = m.group() ;
			paramStr = paramStr.substring(1, paramStr.length()-1) ;
			Connection conn = null;
			try {
				conn = getConnection("jdbc:impala://localhost:21050/", "test", "", "");
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			//retrieve datatype list for each function parameter
			String[] params = paramStr.split(",") ;
			Map<Integer, List<String>> paramMap = new HashMap<Integer, List<String>>() ;
			DataTypeService dtService = new DataTypeService() ;
			for(int i=0; i<params.length; i++){
				List<String> datatypes = dtService.getDataTypesByTag(params[i].trim());
				paramMap.put(i, datatypes) ;
			}
			
			//generate function param combinations and test them.
			Map<String[], Boolean> paramCombinates = getFuncParamCombinations(paramMap) ;
			for(Entry<String[], Boolean> entry : paramCombinates.entrySet()){
				String[] paramArray = entry.getKey() ;
				if(executeFunction(conn, funcSqlStr, paramArray)){
					entry.setValue(true) ;
				}
				System.out.println(entry.getKey()[0] + " : " + entry.getValue());
			}
			
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
		}		
		
		 Map<String, String> resultMap = new LinkedHashMap<String, String>() ;
		 return resultMap ;
		
	}
	

	private boolean executeFunction(Connection conn, String funcSqlStr, String[] paramArray) {
		PreparedStatement ps = null ;
		try {
			for(int i=0; i<paramArray.length; i++){
				String paramName = paramArray[i] ;
				if(paramName.startsWith("~")){
					return true ;
				}
				
				Object objVal = null ;
				switch(paramName){
					case "@bit": 		
					case "@boolean":	funcSqlStr = funcSqlStr.replaceFirst("\\?", String.valueOf(Boolean.TRUE)) ; break;
					case "@integer": 	funcSqlStr = funcSqlStr.replaceFirst("\\?", String.valueOf(Integer.MAX_VALUE)) ; break;
					case "@smallint":	funcSqlStr = funcSqlStr.replaceFirst("\\?", String.valueOf(Short.MAX_VALUE)) ; break;
					case "@tinyint":	funcSqlStr = funcSqlStr.replaceFirst("\\?", String.valueOf(Byte.MAX_VALUE)) ; break;
					case "@bigint":		funcSqlStr = funcSqlStr.replaceFirst("\\?", String.valueOf(Long.MAX_VALUE)) ; break;
					case "@real":		funcSqlStr = funcSqlStr.replaceFirst("\\?", String.valueOf(Float.MAX_VALUE)) ; break;
					case "@float":		funcSqlStr = funcSqlStr.replaceFirst("\\?", String.valueOf(Float.MAX_EXPONENT)) ; break;
					case "@double":		funcSqlStr = funcSqlStr.replaceFirst("\\?", String.valueOf(Double.MAX_VALUE)) ; break;
					case "@decimal":	
					case "@numeric":	funcSqlStr = funcSqlStr.replaceFirst("\\?", String.valueOf(Float.MAX_EXPONENT)) ; break;
					case "@char":		
					case "@varchar":	
					case "@longvarchar":
					case "@varchar2":	
					case "@nchar":		
					case "@nvarchar":	funcSqlStr = funcSqlStr.replaceFirst("\\?", "testing") ; break;
					case "@binary":		funcSqlStr = funcSqlStr.replaceFirst("\\?", null) ; break;
					case "@varbinary":	
					case "@date":		funcSqlStr = funcSqlStr.replaceFirst("\\?", String.valueOf(new Date(System.currentTimeMillis()))) ; break;
					case "@time":		funcSqlStr = funcSqlStr.replaceFirst("\\?", String.valueOf(new Time(System.currentTimeMillis()))) ; break;
					case "@timestamp":	funcSqlStr = funcSqlStr.replaceFirst("\\?", String.valueOf(new Timestamp(System.currentTimeMillis()))) ; break;
					case "@clob":		
					case "@blob":		
					case "@null":		funcSqlStr = funcSqlStr.replaceFirst("\\?", "NULL") ; break;
					case "@interval_year_to_month":	
					case "@interval_year":	
					case "@interval_day":	
					case "@interval_hour":	
					case "@interval_minute":	
					case "@interval_second":	
					case "@interval_day_to_hour":	
					case "@interval_day_to_minute":	
					case "@interval_day_to_second":	
					case "@interval_hour_to_minute":
					case "@interval_hour_to_second":	
					case "@interval_minute_to_second":	break ;
					case "@xml":		
						
				}//end switch
			}//end for
			
			String sql = "select " + funcSqlStr + " from alldatatypes;" ;
			ps = conn.prepareStatement(sql);
			return ps.execute() ;
			
		} catch (SQLException e) {
			e.printStackTrace();
			//ignore all exceptions here, because we only care about if sql could be executed successfully.
		} finally{
			if(ps!=null){
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} 
			}
		}
		
		return false;
	}


	public Map<String[], Boolean> getFuncParamCombinations(Map<Integer, List<String>> datatypes){
		Map<String[], Boolean> combinates = new LinkedHashMap<String[], Boolean>() ;
		getFuncParamCombinations(datatypes, 0, new String[datatypes.size()], combinates) ;
		return combinates ;
	}
	
	private void getFuncParamCombinations(Map<Integer, List<String>> datatypes, int currIdx, String[] resArray, Map<String[], Boolean> resMap){
		if(currIdx==datatypes.size()){
			String[] combinates = new String[resArray.length] ;
			System.arraycopy(resArray, 0, combinates, 0, resArray.length) ;
			resMap.put(combinates, false) ;
			return ;
		}
		List<String> datatypeList = datatypes.get(currIdx) ;
		for(int i=0; i<datatypeList.size(); i++){
			resArray[currIdx] = datatypeList.get(i) ;
			getFuncParamCombinations(datatypes, currIdx+1, resArray, resMap) ;
		}
		
	}
	
	
	public static void main(String[] args){
		FunctionService funcService = new FunctionService() ;
		funcService.testFunction("MAX(~number)", "MAX(?)") ;
		
		
		
//		DataTypeService dtService = new DataTypeService() ;
//		List<String> datatypesParam1 = dtService.getDataTypesByTag("~number") ;
//		List<String> datatypesParam2 = dtService.getDataTypesByTag("~string") ;
//		List<String> datatypesParam3 = dtService.getDataTypesByTag("~date") ;
//		
//		Map<Integer, List<String>> paramMap = new HashMap<Integer, List<String>>() ;
//		paramMap.put(0, datatypesParam1) ;
//		paramMap.put(1, datatypesParam2) ;
//		paramMap.put(2, datatypesParam3) ;
//		
//		Map<String[], Boolean> resMap = funcService.getFuncParamCombinations(paramMap) ;
//		
//		for(String[] array : resMap.keySet()){
//			System.out.println(array[0]+", "+array[1]+", "+array[2]);
//		}
		
	}


	
	
}
