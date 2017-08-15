package net.yxy.dagger.core.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class JdbcDriverService {
	
	private String path ;
	private String className ;
	private String url ;
	private String username ;
	private String password ;
	
	public JdbcDriverService(String path, String className, String url, String username, String password){
		this.path = path ;
		this.className = className ;
		this.url = url ;
		this.username = username ;
		this.password = password ;
	}
	
//	public Path uploadDriver(File file){
//		//TODO:upload file into somewhere and get the url info
//		path = Paths.get("/Users/xianyiye/Documents/Third-Parts/Cloudera_ImpalaJDBC41_2.5.36") ;
//		return path ;
//	}
	
	private URL[] getJDBCJarFiles(){
		Path dirPath = Paths.get(path) ;
		List<URL> urls = new ArrayList<URL>() ;
		try (final DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*.jar")) {
			    stream.forEach(
			    		jarFile -> {
							try {
								urls.add(new URL("jar:file:"+jarFile+"!/")) ;
							} catch (Exception e) {
								e.printStackTrace();
							}
			    });
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally{
			
		}
	
		return urls.toArray(new URL[urls.size()]) ; 
	}
	
	private void registerDriver(String className, URL[] jarUrls){
		URLClassLoader ucl = new URLClassLoader(jarUrls);
		try {
			Driver d = (Driver)Class.forName(className, true, ucl).newInstance();
			DriverManager.registerDriver(new DriverShim(d));
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public Connection createConnection() throws SQLException{
		URL[] jarFiles = getJDBCJarFiles() ;
		registerDriver(className, jarFiles) ;
		return DriverManager.getConnection(url, username, password);
	}
	
	public void createTestSchema(List<String> supportedDataTypes){
		if(supportedDataTypes==null || supportedDataTypes.size()==0){
			supportedDataTypes = new ArrayList<String>() ;
			try {
				Connection conn = createConnection();
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
		
		
		Connection conn = null;
		PreparedStatement ps = null ;
		try {
			conn = createConnection();
			ps = conn.prepareStatement(sb.toString()) ;
			System.out.println(sb.toString()) ;
			ps.executeUpdate() ;
			ps.close();
			
			ps = conn.prepareStatement(sb2.toString()) ;
			System.out.println(sb2.toString()) ;
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
	
	
	public static void main(String[] args) throws Exception {
		JdbcDriverService jdbcDriverService = new JdbcDriverService("/Users/xianyiye/Documents/Third-Parts/Cloudera_ImpalaJDBC41_2.5.36", 
				"com.cloudera.impala.jdbc41.Driver", "jdbc:impala://172.23.5.144:21050/default", "", "") ;
		Connection conn = jdbcDriverService.createConnection() ;
		
		String sql = "select * from bt_string" ;
		Statement stmt = conn.createStatement() ;
		ResultSet rs = stmt.executeQuery(sql) ;
		while(rs.next()){
			System.out.println(rs.getString("c_id"));
		}
		rs.close();
		stmt.close();
		conn.close();
	
	}

}
