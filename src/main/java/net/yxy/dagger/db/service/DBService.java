package net.yxy.dagger.db.service;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class DBService {
	
	private String url ;
	private String dbName ;
	private String username ;
	private String password ;
	
	private static Method addURL = initAddMethod();
    private static URLClassLoader classloader = (URLClassLoader) ClassLoader.getSystemClassLoader(); 

    private static Method initAddMethod() {
        try {
            Method add = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
            add.setAccessible(true);
            return add;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**  
     * load jar classpath。 
     */ 
    public static void loadClasspath() { 
        List<String> files = getJarFiles(); 
        for (String f : files) { 
            loadClasspath(f); 
        } 

        List<String> resFiles = getResFiles(); 
        for (String r : resFiles) { 
            loadResourceDir(r); 
        } 
    } 
  

    private static void loadClasspath(String filepath) { 
        File file = new File(filepath); 
        loopFiles(file); 
    } 

    private static void loadResourceDir(String filepath) { 
        File file = new File(filepath); 
        loopDirs(file); 
    } 


    private static void loopDirs(File file) { 
        if (file.isDirectory()) { 
            addURL(file); 
            File[] tmps = file.listFiles(); 
            for (File tmp : tmps) { 
                loopDirs(tmp); 
            } 
        } 
    } 


    private static void loopFiles(File file) { 
        if (file.isDirectory()) { 
            File[] tmps = file.listFiles(); 
            for (File tmp : tmps) { 
                loopFiles(tmp); 
            } 
        } 
        else { 
            if (file.getAbsolutePath().endsWith(".jar") || file.getAbsolutePath().endsWith(".zip")) { 
                addURL(file); 
            } 
        } 

    } 
      

    private static void addURL(File file) { 
        try { 
            addURL.invoke(classloader, new Object[] { file.toURI().toURL() }); 
        } 
        catch (Exception e) { 
        } 

    } 

  
    private static List<String> getJarFiles() { 
        return null; 
    } 


    private static List<String> getResFiles() { 
        return null; 
    }

	public DBService(String url, String dbName, String username, String password) {
		this.url = url ;
		this.dbName = dbName ;
		this.username = username ;
		this.password = password ;
	}

	public Connection getConnection() throws SQLException {

	    Connection conn = null;
	    Properties connectionProps = new Properties();
	    connectionProps.put("user",username);
	    connectionProps.put("password", password);

        conn = DriverManager.getConnection(url, connectionProps);
	    System.out.println("Connected to database");
	    return conn;
	}
}
