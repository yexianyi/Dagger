package net.yxy.dagger.rest;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import net.yxy.dagger.core.service.FileService;
import net.yxy.dagger.core.service.JdbcDriverService;
import net.yxy.dagger.global.Constants;

/**
 * This example shows how to build Java REST web-service to upload files
 * accepting POST requests with encoding type "multipart/form-data". For more
 * details please read the full tutorial on
 * https://javatutorial.net/java-file-upload-rest-service
 * 
 * @author javatutorial.net
 */
@Path("/service/jdbc")
public class JdbcServiceApi {

	private FileService fileService = new FileService() ;
	
	public JdbcServiceApi() {
	}

	@Context
	private UriInfo context;

	/**
	 * Returns text response to caller containing uploaded file location
	 * 
	 * @return error response in case of missing parameters an internal
	 *         exception or success response if file has been stored
	 *         successfully
	 */
	@POST
	@Path("/uploadAndTest")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadAndTestJdbc(	@FormDataParam("jdbcName") String jdbcName, 
										@FormDataParam("jdbcCls") String jdbcClass, 
										@FormDataParam("jdbcUrl") String jdbcUrl,
										@FormDataParam("jdbcUserName") String jdbcUserName,
										@FormDataParam("jdbcPwd") String jdbcPassword,
										@FormDataParam("file") FormDataBodyPart body) {
		
		try {
			String stortedPath = fileService.uploadFiles(jdbcName, body) ;
			System.out.println("File saved to " + Constants.UPLOAD_FOLDER);
			JdbcDriverService JdbcService = new JdbcDriverService(stortedPath, jdbcClass, jdbcUrl, jdbcUserName, jdbcPassword) ;
			Connection conn = JdbcService.createConnection() ;
			if(conn!=null && !conn.isClosed()){
				conn.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(500).entity(e.getMessage()).build();
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(500).entity("Test connection failed:"+e.getMessage()).build();
		} 

		return Response.status(200)
				.entity("Test Connection succeed!").build();
	}

	
	
	public static void main(String[] args) throws IOException 
	{
	    final javax.ws.rs.client.Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
	 
	    final FileDataBodyPart filePart = new FileDataBodyPart("file", new File("/Users/xianyiye/Downloads/win2012.pem"));
	    FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
	    final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.field("foo", "bar").bodyPart(filePart);
	      
	    final WebTarget target = client.target("http://localhost:8080/dagger/api/service/files/upload");
	    final Response response = target.request().post(Entity.entity(multipart, multipart.getMediaType()));
	     System.out.println(response.getStatus());
	     System.out.println(response);
	    //Use response object to verify upload success
	     
	    formDataMultiPart.close();
	    multipart.close();
	}

}