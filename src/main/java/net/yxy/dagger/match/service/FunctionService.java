package net.yxy.dagger.match.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.yxy.dagger.core.service.JdbcDriverService;

public class FunctionService {

	private JdbcDriverService jdbcService = new JdbcDriverService("/Users/xianyiye/Documents/Third-Parts/Cloudera_ImpalaJDBC41_2.5.36", 
			"com.cloudera.impala.jdbc41.Driver", "jdbc:impala://172.23.5.144:21050/default", "", "") ;
//	private static Map<String, String> standardFunctionMap = new LinkedHashMap<String, String>()  ;
	private static Map<String, String> funcMappings = new LinkedHashMap<String, String>()  ;
	
	
	static{
//		loadFile(standardFunctionMap, "/functionEntities") ;
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
	
	
	@Deprecated
	public Map<String, String> getStandardFunctionMap(){
        return null ;
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
	
	
	public void createTestSchema(List<String> supportedDataTypes){
		if(supportedDataTypes==null || supportedDataTypes.size()==0){
			supportedDataTypes = new ArrayList<String>() ;
			try {
				Connection conn = jdbcService.createConnection();
				DatabaseMetaData metadata = conn.getMetaData();
				ResultSet resultSet = metadata.getTypeInfo();
				while (resultSet.next()) {
					String typeName = resultSet.getString("TYPE_NAME");
					//	      String precision = resultSet.getString("PRECISION"); 
					//	      String maxScale = resultSet.getString("MAXIMUM_SCALE"); 
					//	      String minScale = resultSet.getString("MINIMUM_SCALE"); 
					//	      String numPrecRadix = resultSet.getString("NUM_PREC_RADIX"); 
					//	      System.out.println("Type Name = " + typeName + " | PRECISION="+precision + " | MAXIMUM_SCALE="+maxScale + " | MINIMUM_SCALE=" + minScale + " | NUM_PREC_RADIX="+numPrecRadix);
					supportedDataTypes.add(typeName) ;
				}
				resultSet.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		
		StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS alldatatypes(") ;
		
		for(String datatype : supportedDataTypes){
			switch(datatype.toUpperCase()){
				case "TINYINT": sb.append("TINYINT_COL TINYINT,") ; break ;
				case "SMALLINT": sb.append("SMALLINT_COL SMALLINT,") ; break ;
				case "INT": sb.append("INT_COL INT,") ; break ;
				case "BIGINT": sb.append("BIGINT_COL BIGINT,") ; break ;
				case "REAL": sb.append("REAL_COL REAL,") ; break ;
				case "FLOAT": sb.append("FLOAT_COL FLOAT,") ; break ;
				case "DECIMAL": sb.append("DECIMA_COL DECIMAL(38,38),") ; break ;
				case "DOUBLE": sb.append("DOUBLE_COL DOUBLE,") ; break ;
				case "BOOLEAN": sb.append("BOOLEAN_COL BOOLEAN,") ; break ;
				case "CHAR": sb.append("CHAR_COL CHAR(1),") ; break ;
				case "VARCHAR": sb.append("VARCHAR_COL VARCHAR,") ; break ;
				case "STRING": sb.append("STRING_COL STRING,") ; break ;
				case "TIMESTAMP": sb.append("TIMESTAMP_COL TIMESTAMP,") ; break ;
				
				default: sb.append(datatype+"_COL " + datatype + ",") ; break ;
			}
		}
		
		sb.deleteCharAt(sb.length()-1) ;
		sb.append(")") ;
		
		Random random = new Random(System.currentTimeMillis());
		StringBuilder sb2 = new StringBuilder("INSERT INTO alldatatypes VALUES(") ;
		for(String datatype : supportedDataTypes){
			switch(datatype.toUpperCase()){
				case "TINYINT": sb2.append(random.nextInt(127)).append(",") ; break ;
				case "SMALLINT": sb2.append(random.nextInt(32767)).append(",") ; break ;
				case "INT": sb2.append(random.nextInt(2147483647)).append(",") ; break ;
				case "BIGINT": sb2.append(random.nextLong()).append(",") ; break ;
				case "REAL": sb2.append(random.nextFloat()).append(",") ; break ;
				case "FLOAT": sb2.append(random.nextFloat()).append(",") ; break ;
				case "DECIMAL": sb2.append(BigDecimal.valueOf(random.nextDouble()).setScale(38, RoundingMode.HALF_UP).doubleValue()).append(",") ; break ;
				case "DOUBLE": sb2.append(random.nextDouble()).append(",") ; break ;
				case "BOOLEAN": sb2.append(random.nextBoolean()).append(",") ; break ;
				case "CHAR": sb2.append("NULL,") ; break ;
				case "VARCHAR": sb2.append("'").append(UUID.randomUUID()).append("',") ; break ;
				case "STRING":sb2.append("'").append(UUID.randomUUID()).append("',") ; break ;
				case "TIMESTAMP": sb2.append("'").append("2017-06-20 15:37:28.633").append("',"); break ;
			}
		}
		
		sb2.deleteCharAt(sb2.length()-1) ;
		sb2.append(")") ;
		
		System.out.println(sb2.toString()) ;
		
		Connection conn = null;
		PreparedStatement ps = null ;
		try {
			conn = jdbcService.createConnection();
			ps = conn.prepareStatement(sb.toString()) ;
			ps.executeUpdate() ;
			ps.close();
			
			ps = conn.prepareStatement(sb2.toString()) ;
			ps.executeUpdate() ;
			ps.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		
	}
	
	public Map<String[], Boolean> testFunction(String funcDefStr, String funcSqlStr){
		//funcDefStr =  CORR(~number,~string) ;
		Map<String[], Boolean> paramCombinates = null ;
		Pattern pattern = Pattern.compile("\\(.*?\\)");
		Matcher m = pattern.matcher(funcDefStr);
		if(m.find()){
			String paramStr = m.group() ;
			paramStr = paramStr.substring(1, paramStr.length()-1) ;
			Connection conn = null;
			try {
				conn = jdbcService.createConnection();
				
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
			//paramCombinates is used for recording testing results for each combination
			paramCombinates = getFuncParamCombinations(paramMap) ;
			for(Entry<String[], Boolean> entry : paramCombinates.entrySet()){
				String[] paramArray = entry.getKey() ;
				Boolean result = executeFunction(conn, funcSqlStr, paramArray) ;
				if(result==null){
					entry.setValue(null) ;
				}
				else if(result==true){
					entry.setValue(true) ;
				} 
				System.out.println(entry.getKey()[0] + ", "+ entry.getKey()[1]+ " : " + entry.getValue());
			}
			
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
		}		
		
		 return paramCombinates ;
		
	}
	

	private Boolean executeFunction(Connection conn, String funcSqlStr, String[] paramArray) {
		PreparedStatement ps = null ;
		try {
			for(int i=0; i<paramArray.length; i++){
				String paramName = paramArray[i] ;
				if(paramName.startsWith("~")){
					return null ;
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
//			System.out.println(sql) ;
			ps = conn.prepareStatement(sql);
			return ps.execute() ;
			
		} catch (SQLException e) {
//			e.printStackTrace();
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
	
	private void getFuncParamCombinations(Map<Integer, List<String>> datatypes, int currIdx, String[] resArray, Map<String[], Boolean> resultMap){
		if(currIdx==datatypes.size()){
			String[] resArrayCopy = new String[resArray.length] ;
			System.arraycopy(resArray, 0, resArrayCopy, 0, resArray.length) ;
			resultMap.put(resArrayCopy, false) ;
			return ;
		}
		List<String> datatypeList = datatypes.get(currIdx) ;
		for(int i=0; i<datatypeList.size(); i++){
			resArray[currIdx] = datatypeList.get(i) ;
			getFuncParamCombinations(datatypes, currIdx+1, resArray, resultMap) ;
		}
		
	}
	
	
	/**
	 * 	{"@bigint", "~whole_number"} , null
		{"@bigint", "@bit"} , true
		{"@bigint", "@smallint"} , true
		{"@bigint", "@integer"} , true
		{"@bigint", "@tinyint"} , true
		{"@bigint", "@bigint"} , true
	 * @param resultMap
	 * @return
	 */
	public void consolidateResults(Map<String[], Boolean> resultMap){
		DataTypeService dtService = new DataTypeService() ;
		List<String[]> resMapKeyList = new ArrayList<String[]>(resultMap.keySet());
		int argLength = resMapKeyList.get(0).length ;
		
		Map<String, String[]> tempMap = new HashMap<String, String[]>() ;
		for(int currArgIdx=argLength-1; currArgIdx>=0; currArgIdx--){
				for(int i=resMapKeyList.size()-1; i >=0 ; i--) {
					String[] key = resMapKeyList.get(i) ;
					String currDataType = key[currArgIdx] ;
					
					Boolean result = resultMap.get(key) ;
					if(result == Boolean.TRUE){
						tempMap.put(currDataType, key) ;
					}
					
					if(result == Boolean.FALSE){
						if(tempMap.containsKey(currDataType)){
							tempMap.remove(currDataType) ;
						}
					}
					
					if(currDataType.startsWith("~")){
						//
						// handle the case: 
						// 	~whole_number, @smallint : null
						//	~whole_number, @integer : null
						//	~whole_number, @tinyint : null
						//	~whole_number, @bigint : true
						//
						if(currDataType.equalsIgnoreCase(resMapKeyList.get(i+1)[currArgIdx])){
							resultMap.put(key, resultMap.get(resMapKeyList.get(i+1))) ;
							continue ;
						}
						
						Set<String> children = dtService.getChildDataTypeByTag(currDataType) ;
						if(tempMap.keySet().containsAll(children)){	//all of child are pass -> mark parent testing result as TRUE.
							resultMap.put(key, true) ;
							tempMap.put(currDataType, key) ;
						}else{	//one or more child are fail -> mark parent testing result as FALSE.
							resultMap.put(key, false) ;
							tempMap.remove(currDataType) ;
						}
						
						//remove items which has been used from cache set
						for(String itemKey : children){
							tempMap.remove(itemKey);
						}
					}
					
				}
		}
		
	}
	
	
	public void filterResults(Map<String[], Boolean> resultMap){
		DataTypeService dtService = new DataTypeService() ;
		List<String[]> resMapKeyList = new ArrayList<String[]>(resultMap.keySet());
		int argLength = resMapKeyList.get(0).length ;
		
		Set<String> removeSet = new HashSet<String>() ;
		for(int currArgIdx=argLength-1; currArgIdx>=0; currArgIdx--){
			removeSet.clear();
			Iterator<Map.Entry<String[], Boolean>> iter = resultMap.entrySet().iterator();
			while (iter.hasNext()) {
			    Map.Entry<String[], Boolean> entry = iter.next();
			    if(entry.getValue()==Boolean.FALSE){
			    	iter.remove();
			    	continue;
			    }else{ //Boolean.TRUE
			    	String currDataType = entry.getKey()[currArgIdx] ;
			    	if(removeSet.contains(currDataType)){
			    		removeSet.remove(currDataType);
			    		iter.remove();
			    		continue ;
			    	}
			    	
			    	if(currDataType.startsWith("~")){ //all of its children element should be removed.
			    		removeSet.addAll(dtService.getDataTypesByTag(currDataType)) ;
			    	}
			    	
			    }
			    
			}
		}
		
	}
	
	public List<String> generateFuncMapping(String funcName, Map<String[], Boolean> resultMap){
		List<String> functionMappingList = new ArrayList<String>() ;
		List<String[]> resMapKeyList = new ArrayList<String[]>(resultMap.keySet());
		int argLength = resMapKeyList.get(0).length ;
		
		for(String[] list : resMapKeyList){
			String funcMapping = funcName + "(" ;
			for(int i=0; i<list.length ; i++){
				funcMapping += list[i] ;
				if(i<list.length-1){
					funcMapping += ", " ;
				}
			}
			
			funcMapping += "): " + funcName + "(" ;
			
			for(int i=0; i<argLength; i++){
				funcMapping += "$"+ (i+1) ;
				if(i<argLength-1){
					funcMapping += ", " ;
				}
			}
			funcMapping += ")" ;
			
			functionMappingList.add(funcMapping) ;
		}
		
		return functionMappingList ;
	}
	
	public static void main(String[] args) throws Exception{
		FunctionService funcService = new FunctionService() ;
//		funcService.testFunction("MAX(~number)", "MAX(?)") ;
		Map<String[], Boolean> results = funcService.testFunction("POWER(~number,~number)", "POWER(?, ?)") ;
//		for(Entry<String[], Boolean> entry : results.entrySet()){
//			System.out.println(entry.getKey()[0] + ", "+ entry.getKey()[1]+ " : " + entry.getValue());
//		}
		
		
//		Connection conn = funcService.getConnection("jdbc:impala://localhost:21050/", "test", "", "");
//		DatabaseMetaData metadata = conn.getMetaData();
//		ResultSet resultSet = metadata.getTypeInfo();
//	    while (resultSet.next()) {
//	      String typeName = resultSet.getString("TYPE_NAME");
//	      String createsParams = resultSet.getString("CREATE_PARAMS"); 
//	      String precision = resultSet.getString("PRECISION"); 
//	      String maxScale = resultSet.getString("MAXIMUM_SCALE"); 
//	      String minScale = resultSet.getString("MINIMUM_SCALE"); 
//	      String numPrecRadix = resultSet.getString("NUM_PREC_RADIX"); 
//	      System.out.println("Type Name = " + typeName + " | CREATE_PARAMS=" + createsParams +" | PRECISION="+precision + " | MAXIMUM_SCALE="+maxScale + " | MINIMUM_SCALE=" + minScale + " | NUM_PREC_RADIX="+numPrecRadix);
//	    }
//	    resultSet.close();
//	    conn.close();
		
		
//		List<String> supportedDataTypes = new ArrayList<String>() ;
//		supportedDataTypes.add("TINYINT") ;
//		supportedDataTypes.add("SMALLINT") ;
//		supportedDataTypes.add("INT") ;
//		supportedDataTypes.add("BIGINT") ;
//		supportedDataTypes.add("REAL") ;
//		supportedDataTypes.add("FLOAT") ;
//		supportedDataTypes.add("DECIMAL") ;
//		supportedDataTypes.add("DOUBLE") ;
//		supportedDataTypes.add("BOOLEAN") ;
//		supportedDataTypes.add("CHAR") ;
//		supportedDataTypes.add("VARCHAR") ;
//		supportedDataTypes.add("STRING") ;
//		supportedDataTypes.add("TIMESTAMP") ;
//		funcService.createTestSchema(supportedDataTypes) ;
		
		
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
		
		
		Map<String[], Boolean> resultMap = new LinkedHashMap<String[], Boolean>(){{
			put(new String[]{"~number", "~number"} , null);
			put(new String[]{"~number", "~floating_point"} , null);
			put(new String[]{"~number", "@numeric"} , null);
			put(new String[]{"~number", "@real"} , null);
			put(new String[]{"~number", "@double"} , null);
			put(new String[]{"~number", "@decimal"} , null);
			put(new String[]{"~number", "@float"} , null);
			put(new String[]{"~number", "~whole_number"} , null);
			put(new String[]{"~number", "@bit"} , null);
			put(new String[]{"~number", "@smallint"} , null);
			put(new String[]{"~number", "@integer"} , null);
			put(new String[]{"~number", "@tinyint"} , null);
			put(new String[]{"~number", "@bigint"} , null);
			put(new String[]{"~floating_point", "~number"} , null);
			put(new String[]{"~floating_point", "~floating_point"} , null);
			put(new String[]{"~floating_point", "@numeric"} , null);
			put(new String[]{"~floating_point", "@real"} , null);
			put(new String[]{"~floating_point", "@double"} , null);
			put(new String[]{"~floating_point", "@decimal"} , null);
			put(new String[]{"~floating_point", "@float"} , null);
			put(new String[]{"~floating_point", "~whole_number"} , null);
			put(new String[]{"~floating_point", "@bit"} , null);
			put(new String[]{"~floating_point", "@smallint"} , null);
			put(new String[]{"~floating_point", "@integer"} , null);
			put(new String[]{"~floating_point", "@tinyint"} , null);
			put(new String[]{"~floating_point", "@bigint"} , null);
			put(new String[]{"@numeric", "~number"} , null);
			put(new String[]{"@numeric", "~floating_point"} , null);
			put(new String[]{"@numeric", "@numeric"} , true);
			put(new String[]{"@numeric", "@real"} , false);
			put(new String[]{"@numeric", "@double"} , false);
			put(new String[]{"@numeric", "@decimal"} , true);
			put(new String[]{"@numeric", "@float"} , true);
			put(new String[]{"@numeric", "~whole_number"} , null);
			put(new String[]{"@numeric", "@bit"} , true);
			put(new String[]{"@numeric", "@smallint"} , true);
			put(new String[]{"@numeric", "@integer"} , true);
			put(new String[]{"@numeric", "@tinyint"} , true);
			put(new String[]{"@numeric", "@bigint"} , true);
			put(new String[]{"@real", "~number"} , null);
			put(new String[]{"@real", "~floating_point"} , null);
			put(new String[]{"@real", "@numeric"} , true);
			put(new String[]{"@real", "@real"} , true);
			put(new String[]{"@real", "@double"} , true);
			put(new String[]{"@real", "@decimal"} , true);
			put(new String[]{"@real", "@float"} , true);
			put(new String[]{"@real", "~whole_number"} , null);
			put(new String[]{"@real", "@bit"} , true);
			put(new String[]{"@real", "@smallint"} , true);
			put(new String[]{"@real", "@integer"} , true);
			put(new String[]{"@real", "@tinyint"} , true);
			put(new String[]{"@real", "@bigint"} , true);
			put(new String[]{"@double", "~number"} , null);
			put(new String[]{"@double", "~floating_point"} , null);
			put(new String[]{"@double", "@numeric"} , true);
			put(new String[]{"@double", "@real"} , true);
			put(new String[]{"@double", "@double"} , true);
			put(new String[]{"@double", "@decimal"} , true);
			put(new String[]{"@double", "@float"} , true);
			put(new String[]{"@double", "~whole_number"} , null);
			put(new String[]{"@double", "@bit"} , true);
			put(new String[]{"@double", "@smallint"} , true);
			put(new String[]{"@double", "@integer"} , true);
			put(new String[]{"@double", "@tinyint"} , true);
			put(new String[]{"@double", "@bigint"} , true);
			put(new String[]{"@decimal", "~number"} , null);
			put(new String[]{"@decimal", "~floating_point"} , null);
			put(new String[]{"@decimal", "@numeric"} , true);
			put(new String[]{"@decimal", "@real"} , true);
			put(new String[]{"@decimal", "@double"} , true);
			put(new String[]{"@decimal", "@decimal"} , true);
			put(new String[]{"@decimal", "@float"} , true);
			put(new String[]{"@decimal", "~whole_number"} , null);
			put(new String[]{"@decimal", "@bit"} , true);
			put(new String[]{"@decimal", "@smallint"} , true);
			put(new String[]{"@decimal", "@integer"} , true);
			put(new String[]{"@decimal", "@tinyint"} , true);
			put(new String[]{"@decimal", "@bigint"} , true);
			put(new String[]{"@float", "~number"} , null);
			put(new String[]{"@float", "~floating_point"} , null);
			put(new String[]{"@float", "@numeric"} , true);
			put(new String[]{"@float", "@real"} , true);
			put(new String[]{"@float", "@double"} , true);
			put(new String[]{"@float", "@decimal"} , true);
			put(new String[]{"@float", "@float"} , true);
			put(new String[]{"@float", "~whole_number"} , null);
			put(new String[]{"@float", "@bit"} , true);
			put(new String[]{"@float", "@smallint"} , true);
			put(new String[]{"@float", "@integer"} , true);
			put(new String[]{"@float", "@tinyint"} , true);
			put(new String[]{"@float", "@bigint"} , true);
			put(new String[]{"~whole_number", "~number"} , null);
			put(new String[]{"~whole_number", "~floating_point"} , null);
			put(new String[]{"~whole_number", "@numeric"} , null);
			put(new String[]{"~whole_number", "@real"} , null);
			put(new String[]{"~whole_number", "@double"} , null);
			put(new String[]{"~whole_number", "@decimal"} , null);
			put(new String[]{"~whole_number", "@float"} , null);
			put(new String[]{"~whole_number", "~whole_number"} , null);
			put(new String[]{"~whole_number", "@bit"} , null);
			put(new String[]{"~whole_number", "@smallint"} , null);
			put(new String[]{"~whole_number", "@integer"} , null);
			put(new String[]{"~whole_number", "@tinyint"} , null);
			put(new String[]{"~whole_number", "@bigint"} , null);
			put(new String[]{"@bit", "~number"} , null);
			put(new String[]{"@bit", "~floating_point"} , null);
			put(new String[]{"@bit", "@numeric"} , true);
			put(new String[]{"@bit", "@real"} , true);
			put(new String[]{"@bit", "@double"} , true);
			put(new String[]{"@bit", "@decimal"} , true);
			put(new String[]{"@bit", "@float"} , true);
			put(new String[]{"@bit", "~whole_number"} , null);
			put(new String[]{"@bit", "@bit"} , true);
			put(new String[]{"@bit", "@smallint"} , true);
			put(new String[]{"@bit", "@integer"} , true);
			put(new String[]{"@bit", "@tinyint"} , true);
			put(new String[]{"@bit", "@bigint"} , true);
			put(new String[]{"@smallint", "~number"} , null);
			put(new String[]{"@smallint", "~floating_point"} , null);
			put(new String[]{"@smallint", "@numeric"} , true);
			put(new String[]{"@smallint", "@real"} , true);
			put(new String[]{"@smallint", "@double"} , true);
			put(new String[]{"@smallint", "@decimal"} , true);
			put(new String[]{"@smallint", "@float"} , true);
			put(new String[]{"@smallint", "~whole_number"} , null);
			put(new String[]{"@smallint", "@bit"} , true);
			put(new String[]{"@smallint", "@smallint"} , true);
			put(new String[]{"@smallint", "@integer"} , true);
			put(new String[]{"@smallint", "@tinyint"} , true);
			put(new String[]{"@smallint", "@bigint"} , true);
			put(new String[]{"@integer", "~number"} , null);
			put(new String[]{"@integer", "~floating_point"} , null);
			put(new String[]{"@integer", "@numeric"} , true);
			put(new String[]{"@integer", "@real"} , true);
			put(new String[]{"@integer", "@double"} , true);
			put(new String[]{"@integer", "@decimal"} , true);
			put(new String[]{"@integer", "@float"} , true);
			put(new String[]{"@integer", "~whole_number"} , null);
			put(new String[]{"@integer", "@bit"} , true);
			put(new String[]{"@integer", "@smallint"} , true);
			put(new String[]{"@integer", "@integer"} , true);
			put(new String[]{"@integer", "@tinyint"} , true);
			put(new String[]{"@integer", "@bigint"} , true);
			put(new String[]{"@tinyint", "~number"} , null);
			put(new String[]{"@tinyint", "~floating_point"} , null);
			put(new String[]{"@tinyint", "@numeric"} , true);
			put(new String[]{"@tinyint", "@real"} , true);
			put(new String[]{"@tinyint", "@double"} , true);
			put(new String[]{"@tinyint", "@decimal"} , true);
			put(new String[]{"@tinyint", "@float"} , true);
			put(new String[]{"@tinyint", "~whole_number"} , null);
			put(new String[]{"@tinyint", "@bit"} , true);
			put(new String[]{"@tinyint", "@smallint"} , true);
			put(new String[]{"@tinyint", "@integer"} , true);
			put(new String[]{"@tinyint", "@tinyint"} , true);
			put(new String[]{"@tinyint", "@bigint"} , true);
			put(new String[]{"@bigint", "~number"} , null);
			put(new String[]{"@bigint", "~floating_point"} , null);
			put(new String[]{"@bigint", "@numeric"} , true);
			put(new String[]{"@bigint", "@real"} , true);
			put(new String[]{"@bigint", "@double"} , true);
			put(new String[]{"@bigint", "@decimal"} , true);
			put(new String[]{"@bigint", "@float"} , true);
			put(new String[]{"@bigint", "~whole_number"} , null);
			put(new String[]{"@bigint", "@bit"} , true);
			put(new String[]{"@bigint", "@smallint"} , true);
			put(new String[]{"@bigint", "@integer"} , true);
			put(new String[]{"@bigint", "@tinyint"} , true);
			put(new String[]{"@bigint", "@bigint"} , true);

		}};
		
		funcService.consolidateResults(resultMap);
		funcService.filterResults(resultMap);
//		for(Entry<String[], Boolean> entry : resultMap.entrySet()){
//			System.out.println(entry.getKey()[0] + ", " + entry.getKey()[1] + " : " + entry.getValue());
//		}
		
		
		
//		Map<String, Integer> map = new LinkedHashMap<String, Integer>(){{
//			put("a",1);
//			put("b",2);
//			put("c",3);
//			
//			}} ;
//			
//			Set<String> set = new HashSet<String>() ;
//			set.add("a") ;
//			set.add("b") ;
//			
//			System.out.println(map.keySet().containsAll(set));
			
		List<String> funcMappings = funcService.generateFuncMapping("count", resultMap) ;
		for(String funcMapping : funcMappings){
			System.out.println(funcMapping);
		}
	}


	
	
}
