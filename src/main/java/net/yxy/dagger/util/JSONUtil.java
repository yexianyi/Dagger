package net.yxy.dagger.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JSONUtil {
	
	private JSONUtil(){
		
	}
	
	private static Logger logger = LoggerFactory.getLogger(JSONUtil.class);

	public static String convertObj(Object obj){
		String jsonRsp = null ;
		ObjectMapper mapper = new ObjectMapper();
		//Object to JSON in String
		try {
			jsonRsp = mapper.writeValueAsString(obj);
			logger.debug(jsonRsp);
			
		} catch (JsonProcessingException e) {
			logger.error("Cannot convert object to JSON, because "+e.getMessage());
		} finally{
			return jsonRsp ;
		}
		
	}
	
	public static boolean hasAttribute(JSONObject jsonObj, String attr)
    {
                Iterator it = jsonObj.keys();
                String key = "";
                while (it.hasNext()) {
                    key = it.next().toString();
                    if (key.equalsIgnoreCase(attr)) {
                        return true ;
                    }
                    
                    try {
                        Object obj = jsonObj.get(key);
                        if(obj instanceof JSONObject)
                        {
                            return hasAttribute((JSONObject)obj, attr);
                        }
                        else if(obj instanceof JSONArray)
                        {
                            JSONArray array = (JSONArray)obj;
                            for(int i = 0; i < array.length(); i++)
                            {
                                return hasAttribute(array.getJSONObject(i), attr);         
                            }
                        }
                        
                    } catch (JSONException e) {
                        logger.error("COMM", "85010", "Exception occurred: "+e.toString());
                        logger.debug("COMM", "85999", e.getStackTrace());
                    }
                }

                return false;
    }
    
    
    public static String findAttribute(JSONObject jsonObj, String attr) {

        Iterator it = jsonObj.keys();
        String key = "";
        while (it.hasNext()) {
            key = it.next().toString();
            if (key.equalsIgnoreCase(attr)) {
                try {
                    return jsonObj.get(key).toString();
                } catch (JSONException e) {
                	logger.error("COMM", "85010", "Exception occurred: "+e.toString());
                	logger.debug("COMM", "85999", e.getStackTrace());
                }
            }
            
            try {
                Object obj = jsonObj.get(key);
                if(obj instanceof JSONObject)
                {
                    return findAttribute((JSONObject)obj, attr);
                }
                else if(obj instanceof JSONArray)
                {
                    JSONArray array = (JSONArray)obj;
                    for(int i = 0; i < array.length(); i++)
                    {
                        return findAttribute(array.getJSONObject(i), attr);         
                    }
                }
                
            } catch (JSONException e) {
            	logger.error("COMM", "85010", "Exception occurred: "+e.toString());
            	logger.debug("COMM", "85999", e.getStackTrace());
            }
        }

        return null;
     
    }
    
    public static JSONObject findAttrObj(JSONObject jsonObj, String attr) {

        Iterator it = jsonObj.keys();
        String key = "";
        while (it.hasNext()) {
            key = it.next().toString();
            if (key.equalsIgnoreCase(attr)) {
                try {
                    return jsonObj.getJSONObject(key);
                } catch (JSONException e) {
                    logger.error("COMM", "85010", "Exception occurred: "+e.toString());
                    logger.debug("COMM", "85999", e.getStackTrace());
                }
            }
            
            try {
                Object obj = jsonObj.get(key);
                if(obj instanceof JSONObject)
                {
                    return findAttrObj((JSONObject)obj, attr);
                }
                else if(obj instanceof JSONArray)
                {
                    JSONArray array = (JSONArray)obj;
                    for(int i = 0; i < array.length(); i++)
                    {
                        return findAttrObj(array.getJSONObject(i), attr);         
                    }
                }
                
            } catch (JSONException e) {
                logger.error("COMM", "85010", "Exception occurred: "+e.toString());
                logger.debug("COMM", "85999", e.getStackTrace());
            }
        }

        return null;
     
    }
    
    
    public static JSONArray findArray(JSONObject jsonObj, String attr) {
            
            Iterator it = jsonObj.keys();
            String key = "";
            while (it.hasNext()) {
                key = it.next().toString();
                if (key.equalsIgnoreCase(attr)) {
                    try {
                        Object obj = jsonObj.get(key);
                        if(obj instanceof JSONArray)
                            return (JSONArray)obj;
                    } catch (JSONException e) {
                        logger.error("COMM", "85010", "Exception occurred: "+e.toString());
                        logger.debug("COMM", "85999", e.getStackTrace());
                    }
                }
                
                try {
                    Object obj = jsonObj.get(key);
                    if(obj instanceof JSONObject)
                    {
                        return findArray((JSONObject)obj, attr);
                    }
                    else if(obj instanceof JSONArray)
                    {
                        JSONArray array = (JSONArray)obj;
                        for(int i = 0; i < array.length(); i++)
                        {
                            return findArray(array.getJSONObject(i), attr);         
                        }
                    }
                    
                } catch (JSONException e) {
                    logger.error("COMM", "85010", "Exception occurred: "+e.toString());
                    logger.debug("COMM", "85999", e.getStackTrace());
                }
            }

            return null;
        }
    
    
    public static List<JSONObject> mergeAllJSONObjects(String[] jsonResArray, String arrayName)
    {
        List<JSONObject> jsonList = new ArrayList<JSONObject>() ;    
        for(int i=0 ; i<jsonResArray.length ; i++)
        {
                String json = jsonResArray[i] ;
                try {
                    JSONArray cacheArray = JSONUtil.findArray(new JSONObject(json), arrayName);
                    if(cacheArray == null){
                        return jsonList;
                    }
                    for(int j=0 ; j<cacheArray.length() ; j++)
                    {
                        jsonList.add(cacheArray.getJSONObject(j)) ;     
                    }
                    
                } catch (JSONException e) {
                    logger.error("COMM", "85010", "Exception occurred: "+e.toString());
                    logger.debug("COMM", "85999", e.getStackTrace());
                }
        }
        
        return jsonList ;
    }
    
    public static String findVolumeUUID(JSONObject jsonObj) {
        try {
            JSONArray array = JSONUtil.findArray(jsonObj, "Log") ;
//            System.out.println(array.length());
            for(int i=0; i<array.length(); i++){
                JSONObject obj = (JSONObject)array.get(i);
                String msg = obj.getString("Msg");
                if(msg.contains("UUID")){
                    String[] msgs = msg.split(":");
                    String uuid = msgs[1].trim();
                    return uuid;
                }
            }
        } catch (JSONException e) {
            logger.error("COMM", "85010", "Exception occurred: "+e.toString());
            logger.debug("COMM", "85999", e.getStackTrace());
        }
        return null;
    }
    
    
    public static String toNamepairs(JSONObject jsonObj) 
    {
        String namepairs = "" ;

        Iterator it = jsonObj.keys();
        String key = "";
        while (it.hasNext()) {
            key = it.next().toString();
            
            try {
                Object obj = jsonObj.get(key);
                if(obj instanceof JSONObject)
                {
                    namepairs += toNamepairs((JSONObject)obj);
                }
                else if(obj instanceof JSONArray)
                {
                    JSONArray array = (JSONArray)obj;
                    for(int i = 0; i < array.length(); i++)
                    {
                        namepairs += toNamepairs(array.getJSONObject(i));         
                    }
                }
                else
                {
                    String value = (String)obj;
                    namepairs += key + ":"+value +";";
                }
            } catch (JSONException e) {
                logger.error("COMM", "85010", "Exception occurred: "+e.toString());
                logger.debug("COMM", "85999", e.getStackTrace());
            }
        }

        
        return namepairs ;
    }
}
