package net.yxy.dagger.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

/**
 * This example shows how to build Java REST web-service to upload files
 * accepting POST requests with encoding type "multipart/form-data". For more
 * details please read the full tutorial on
 * https://javatutorial.net/java-file-upload-rest-service
 * 
 * @author javatutorial.net
 */
@Path("/service/files")
public class FileService {

	/** The path to the folder where we want to store the uploaded files */
	private static final String UPLOAD_FOLDER = FileService.class.getProtectionDomain().getCodeSource().getLocation().getPath() + File.separator + "Resources" + File.separator + "jdbc_drivers" + File.separator;

	public FileService() {
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
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(@FormDataParam("jdbcName") String jdbcName, @FormDataParam("file") FormDataBodyPart body) {
		
		// create our destination folder, if it not exists
		String uploadedFileLocation = UPLOAD_FOLDER + File.separator + jdbcName + File.separator ;
		try {
			createFolderIfNotExists(uploadedFileLocation);
		} catch (SecurityException se) {
			return Response.status(500)
					.entity("Can not create destination folder on server")
					.build();
		}
		
		//upload files
		for(BodyPart part : body.getParent().getBodyParts()){
	        InputStream uploadedInputStream = part.getEntityAs(InputStream.class);
	        ContentDisposition fileDetail = part.getContentDisposition();
	        // check if all form parameters are provided
			if (uploadedInputStream == null || fileDetail == null)
				return Response.status(400).entity("Invalid form data").build();

			uploadedFileLocation =  uploadedFileLocation + fileDetail.getFileName();
			try {
				saveToFile(uploadedInputStream, uploadedFileLocation);
			} catch (IOException e) {
				e.printStackTrace();
				return Response.status(500).entity("Can not save file").build();
			}
	    }
		

		return Response.status(200)
				.entity("File saved to " + UPLOAD_FOLDER).build();
	}

	/**
	 * Utility method to save InputStream data to target location/file
	 * 
	 * @param inStream
	 *            - InputStream to be saved
	 * @param target
	 *            - full path to destination file
	 */
	private void saveToFile(InputStream inStream, String target)
			throws IOException {
		OutputStream out = null;
		int read = 0;
		byte[] bytes = new byte[1024];

		out = new FileOutputStream(new File(target));
		while ((read = inStream.read(bytes)) != -1) {
			out.write(bytes, 0, read);
		}
		out.flush();
		out.close();
	}

	/**
	 * Creates a folder to desired location if it not already exists
	 * 
	 * @param dirName
	 *            - full path to the folder
	 * @throws SecurityException
	 *             - in case you don't have permission to create the folder
	 */
	private void createFolderIfNotExists(String dirName)
			throws SecurityException {
		File theDir = new File(dirName);
		if (!theDir.exists()) {
			theDir.mkdirs();
		}
		
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