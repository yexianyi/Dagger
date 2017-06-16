package net.yxy.dagger.match.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
		
	}
	
	public Map<String, String> testFunction(String funcDefStr, String funcSqlStr){
		//funcDefStr =  CORR(~number,~string) ;
		Pattern pattern = Pattern.compile("\\(.*?\\)");
		Matcher m = pattern.matcher(funcDefStr);
		if(m.find()){
			String paramStr = m.group() ;
			paramStr = paramStr.substring(1, paramStr.length()) ;
			Connection conn = null;
			PreparedStatement ps ;
			try {
				conn = getConnection("jdbc:impala://localhost:21050/", "test", "", "");
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			String[] params = paramStr.split(",") ;
			Map<Integer, List<String>> paramMap = new HashMap<Integer, List<String>>() ;
			DataTypeService dtService = new DataTypeService() ;
			for(int i=0; i<params.length; i++){
				List<String> datatypes = dtService.getDataTypesByTag(params[i].trim());
				paramMap.put(i, datatypes) ;
			}
			
			List<String[]> paramCombinates = getFuncParamCombinations(paramMap) ;
			for(int i=paramCombinates.size()-1; i>=0; i--){
				String[] paramArray = paramCombinates.get(i) ;
				boolean isSuccess = executeFunction(conn, funcSqlStr, paramArray) ;
			}
			
		}		
		
		
		 Map<String, String> resultMap = new LinkedHashMap<String, String>() ;
		 return resultMap ;
		
	}
	

	private boolean executeFunction(Connection conn, String funcSqlStr, String[] paramArray) {
		String sql = "select " + funcSqlStr + " from test.alldatatypes;" ;
		PreparedStatement ps = null ;
		try {
			ps = conn.prepareStatement(sql);
			for(int i=0; i<paramArray.length; i++){
				String paramName = paramArray[i] ;
				Object objVal = null ;
				switch(paramName){
					case "@bit": 		
					case "@boolean":	ps.setBoolean(i+1,(boolean) objVal); break;
					case "@integer": 	ps.setInt(i+1, (int) objVal); break;
					case "@smallint":	ps.setShort(i+1, (short) objVal); break;
					case "@tinyint":	ps.setByte(i+1, (byte) objVal); break;
					case "@bigint":		ps.setLong(i+1, (long) objVal); break;
					case "@float":		ps.setLong(i+1, (long) objVal); break;
					case "@real":		ps.setFloat(i+1, (float) objVal); break;
					case "@double":		ps.setDouble(i+1, (double) objVal); break;
					case "@decimal":	
					case "@numeric":	ps.setBigDecimal(i+1, (BigDecimal) objVal); break;
					case "@char":		
					case "@varchar":	
					case "@varchar2":	ps.setString(i+1, (String) objVal); break;
					case "@longvarchar":
					case "@nchar":		
					case "@nvarchar":	ps.setNString(i+1,(String) objVal); break;
					case "@binary":		ps.setBinaryStream(i+1, null); break;
					case "@varbinary":	ps.setBytes(i+1, (byte[]) objVal); break;
					case "@date":		ps.setDate(i+1, (Date) objVal); break;
					case "@time":		ps.setTime(i+1, (Time) objVal); break;
					case "@timestamp":	ps.setTimestamp(i+1, (Timestamp) objVal); break;
					case "@clob":		ps.setClob(i+1,(Clob) objVal); break;
					case "@blob":		ps.setBlob(i+1,(Blob) objVal); break;
					case "@null":		ps.setNull(i+1,Types.NULL); break;
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
					case "@xml":		ps.setSQLXML(i+1,(SQLXML) objVal); break;
						
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
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


	public List<String[]> getFuncParamCombinations(Map<Integer, List<String>> datatypes){
		List<String[]> combinates = new ArrayList<String[]>() ;
		getFuncParamCombinations(datatypes, 0, new String[datatypes.size()], combinates) ;
		return combinates ;
	}
	
	private void getFuncParamCombinations(Map<Integer, List<String>> datatypes, int currIdx, String[] resArray, List<String[]> resList){
		if(currIdx==datatypes.size()){
//			if(resArray[0].contains("~") || resArray[1].contains("~") || resArray[2].contains("~")){
//				return ;	
//			}
//			System.out.println(resArray[0]+", "+resArray[1]+", "+resArray[2]);
			String[] combinates = new String[resArray.length] ;
			System.arraycopy(resArray, 0, combinates, 0, resArray.length) ;
			resList.add(combinates) ;
			return ;
		}
		List<String> datatypeList = datatypes.get(currIdx) ;
		for(int i=0; i<datatypeList.size(); i++){
			resArray[currIdx] = datatypeList.get(i) ;
			getFuncParamCombinations(datatypes, currIdx+1, resArray, resList) ;
		}
		
	}
	
	
	public static void main(String[] args){
		FunctionService funcService = new FunctionService() ;
		DataTypeService dtService = new DataTypeService() ;
		List<String> datatypesParam1 = dtService.getDataTypesByTag("~number") ;
		List<String> datatypesParam2 = dtService.getDataTypesByTag("~string") ;
		List<String> datatypesParam3 = dtService.getDataTypesByTag("~date") ;
		
		Map<Integer, List<String>> paramMap = new HashMap<Integer, List<String>>() ;
		paramMap.put(0, datatypesParam1) ;
		paramMap.put(1, datatypesParam2) ;
		paramMap.put(2, datatypesParam3) ;
		
		List<String[]> resList = funcService.getFuncParamCombinations(paramMap) ;
		
		for(String[] array : resList){
			System.out.println(array[0]+", "+array[1]+", "+array[2]);
		}
		
		
	}


	
	
}
