package net.yxy.dagger.core.service;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
