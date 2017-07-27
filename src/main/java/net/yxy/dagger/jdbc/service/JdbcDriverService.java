package net.yxy.dagger.jdbc.service;

import java.io.File;
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

	private Path path ;
	private URL[] jarUrls ;
	
	public Path uploadDriver(File file){
		//TODO:upload file into somewhere and get the url info
		path = Paths.get("/Users/xianyiye/Documents/Third-Parts/Cloudera_ImpalaJDBC41_2.5.36") ;
		return path ;
	}
	
	public URL[] getJDBCJarFiles(Path path){
		if(jarUrls==null){
			List<URL> urls = new ArrayList<URL>() ;
			try (final DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.jar")) {
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
				jarUrls = urls.toArray(new URL[urls.size()]) ;
			}
		}
		
		return jarUrls ;
	}
	
	public void registerDriver(String className){
		URLClassLoader ucl = new URLClassLoader(jarUrls);
		try {
			Driver d = (Driver)Class.forName(className, true, ucl).newInstance();
			DriverManager.registerDriver(new DriverShim(d));
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public Connection createConnection(String url, String username, String password) throws SQLException{
		return DriverManager.getConnection(url, username, password);
	}
	
	
	public static void main(String[] args) throws Exception {
		JdbcDriverService jdbcDriverService = new JdbcDriverService() ;
		Path path = jdbcDriverService.uploadDriver(null) ;
		jdbcDriverService.getJDBCJarFiles(path) ;
		jdbcDriverService.registerDriver("com.cloudera.impala.jdbc41.Driver");
		Connection conn = jdbcDriverService.createConnection("jdbc:impala://172.23.5.144:21050/default", "", "") ;
		
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
