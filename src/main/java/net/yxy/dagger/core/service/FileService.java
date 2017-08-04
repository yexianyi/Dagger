package net.yxy.dagger.core.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;

import net.yxy.dagger.global.Constants;


public class FileService {

	public String uploadFiles(String jdbcName, FormDataBodyPart body) throws IOException {

		// create our destination folder, if it not exists
		String uploadedFileLocation = Constants.UPLOAD_FOLDER + File.separator + jdbcName + File.separator;
		createFolderIfNotExists(uploadedFileLocation);
		// upload files
		for (BodyPart part : body.getParent().getBodyParts()) {
			InputStream uploadedInputStream = part.getEntityAs(InputStream.class);
			ContentDisposition fileDetail = part.getContentDisposition();
			if (fileDetail.getFileName() == null) {
				continue;
			}
			// check if all form parameters are provided
			if (uploadedInputStream == null || fileDetail == null)
				return "Invalid form data" ;

			String filePath = uploadedFileLocation + fileDetail.getFileName();
				saveToFile(uploadedInputStream, filePath);
		}

		return uploadedFileLocation ;
	}

	/**
	 * Utility method to save InputStream data to target location/file
	 * 
	 * @param inStream
	 *            - InputStream to be saved
	 * @param target
	 *            - full path to destination file
	 */
	private void saveToFile(InputStream inStream, String target) throws IOException {
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
	private void createFolderIfNotExists(String dirName) throws SecurityException {
		File theDir = new File(dirName);
		if (!theDir.exists()) {
			theDir.mkdirs();
		}

	}


}
