package net.yxy.dagger.main;


import org.apache.activemq.broker.BrokerService;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;

/**
* This class shows how to configure Authentication in programming method.
* @author xianyiye
* 2017/05/19
*/

public class AppMain {
	
	public static void main(String[] args) throws Exception {
		
		//Start ActiveMQ Broker
		BrokerService broker =new BrokerService();  
	    broker.setPersistent(false);
	    broker.setBrokerName("testName");//如果启动多个Broker时，必须为Broker设置一个名称  
	    broker.addConnector("tcp://localhost:61616");  
	    broker.start();  
		
		// Since this example shows off SSL configuration, we need a keystore
       // with the appropriate key. These lookup of jetty.home is purely a hack
       // to get access to a keystore that we use in many unit tests and should
       // probably be a direct path to your own keystore.
//       File keystoreFile = new File("src/main/resources/keystore");
//       if (!keystoreFile.exists())
//       {
//           throw new FileNotFoundException(keystoreFile.getAbsolutePath());
//       }
		
		
       // Create a basic jetty server object without declaring the port. Since
       // we are configuring connectors directly we'll be setting ports on
       // those connectors.
		Server server = new Server(8080);
		
		HttpConfiguration https = new HttpConfiguration();
	    https.addCustomizer(new SecureRequestCustomizer());
	    
       // SSL Context Factory for HTTPS
       // SSL requires a certificate so we configure a factory for ssl contents
       // with information pointing to what keystore the ssl connection needs
       // to know about. Much more configuration is available the ssl context,
       // including things like choosing the particular certificate out of a
       // keystore to be used.
//       SslContextFactory sslContextFactory = new SslContextFactory();
//       sslContextFactory.setKeyStorePath(keystoreFile.getAbsolutePath());
//       sslContextFactory.setKeyStorePassword("OBF:19iy19j019j219j419j619j8"); //123456
//       sslContextFactory.setKeyManagerPassword("OBF:19iy19j019j219j419j619j8"); //123456

//       ServerConnector sslConnector = new ServerConnector(server,
//               new SslConnectionFactory(sslContextFactory, "http/1.1"),
//               new HttpConnectionFactory(https));
//       sslConnector.setPort(8443);
       
       
       // Here you see the server having multiple connectors registered with
       // it, now requests can flow into the server from both http and https
       // urls to their respective ports and be processed accordingly by jetty.
       // A simple handler is also registered with the server so the example
       // has something to pass requests off to.

       // Set the connectors
//       server.setConnectors(new Connector[] {sslConnector});
	    
		// Handler for multiple web apps
		HandlerCollection handlers = new HandlerCollection();

		// Creating the first web application context
		WebAppContext webapp1 = new WebAppContext();
		webapp1.setResourceBase("src/main/webapp");
		webapp1.setContextPath("/dagger");
		
		// Init global functional features
//		webapp1.addEventListener(new InitApplication());
		
		// Configure LoginService which is required by each context/webapp 
		// that has a authentication mechanism, which is used to check the 
		// validity of the username and credentials collected by the 
		//authentication mechanism. Jetty provides the following implementations 
		// of LoginService:
		//		HashLoginService
		//		A user realm that is backed by a hash map that is filled either programatically or from a java properties file.
		//		JDBCLoginService
		//		Uses a JDBC connection to an SQL database for authentication
		//		DataSourceLoginService
		//		Uses a JNDI defined DataSource for authentication
		//		JAASLoginService
		//		Uses a JAAS provider for authentication, See the section on JAAS support for more information.
		//		SpnegoLoginService
		//		SPNEGO Authentication, See the section on SPNEGO support for more information.
//       LoginService loginService = new HashLoginService("MyRealm", "src/main/resources/realm.properties");
//       server.addBean(loginService);
		
		
		 // This constraint requires authentication and in addition that an
       // authenticated user be a member of a given set of roles for
       // authorization purposes.
//       Constraint constraint = new Constraint();
//       constraint.setName("auth");
//       constraint.setAuthenticate(true);
//       constraint.setRoles(new String[] { "user", "admin" });
		
		// Binds a url pattern with the previously created constraint. The roles
       // for this constraing mapping are mined from the Constraint itself
       // although methods exist to declare and bind roles separately as well.
//       ConstraintMapping mapping = new ConstraintMapping();
//       mapping.setPathSpec("/*");
//       mapping.setConstraint(constraint);
		
       
       // A security handler is a jetty handler that secures content behind a
       // particular portion of a url space. The ConstraintSecurityHandler is a
       // more specialized handler that allows matching of urls to different
       // constraints. The server sets this as the first handler in the chain,
       // effectively applying these constraints to all subsequent handlers in
       // the chain.
//       ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();
//		securityHandler.setConstraintMappings(Collections.singletonList(mapping));
//		securityHandler.setAuthenticator(new FormAuthenticator("/logon.html","/logerror.html",false)); // BASIC/FORM AUTHMETHOD
//		securityHandler.setLoginService(loginService);
//		
//		webapp1.setSecurityHandler(securityHandler);
		
		// do not need webdefault.xml. If uncomment this line, this file will be
		// load before each of web.xml files.
//		webapp1.setDefaultsDescriptor("src/main/webdefault/webdefault.xml");
		handlers.addHandler(webapp1);

		// Creating the second web application context
//		WebAppContext webapp2 = new WebAppContext();
//		webapp2.setResourceBase("src/main/webapp2");
//		webapp2.setContextPath("/webapp2");
//		webapp2.setDefaultsDescriptor("src/main/webdefault/webdefault.xml");
//		handlers.addHandler(webapp2);

		// Adding the handlers to the server
		server.setHandler(handlers);

		server.getStopAtShutdown() ;
		// Starting the Server
		server.start();
		System.out.println("Started!");
		server.join();


	}
}
