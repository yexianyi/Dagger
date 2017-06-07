package net.yxy.dagger.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TrainFileGenerator {
	
	public static List<String> readRawTrainFile(String filePath){
		  List<String> list = new ArrayList<String>();
	        try
	        {
	            String encoding = "UTF-8";
	            File file = new File(filePath);
	            if (file.isFile() && file.exists()){
	                InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
	                BufferedReader bufferedReader = new BufferedReader(read);
	                String lineTxt = null;

	                while ((lineTxt = bufferedReader.readLine()) != null){
	                    list.add(handleLine(lineTxt));
	                }
	                bufferedReader.close();
	                read.close();
	            }
	            else
	            {
	                System.out.println("Cannot find specific file");
	            }
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }

	        return list;
	}
	

	private static String handleLine(String lineTxt) {
		lineTxt.replaceAll("ARRAY", "<START:datatype> ARRAY <END>") ;
		return null;
	}


	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
