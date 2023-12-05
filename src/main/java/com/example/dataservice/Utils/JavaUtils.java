package com.example.dataservice.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.*;

public class JavaUtils {

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keyItr = object.keys();
        while(keyItr.hasNext()) {
            String key = keyItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray){
                value = toList((JSONArray) value);
            }else if( value instanceof  JSONObject){
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++){
            Object value = array.get(i);
            if(value instanceof JSONArray){
                value = toList((JSONArray) value);
            }else if(value instanceof JSONObject){
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    public static Map<String, Object> jsonToMap(String jsonString) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();
        JSONObject json = new JSONObject(jsonString);
        if(!json.equals(JSONObject.NULL)){
            retMap = toMap(json);
        }
        return retMap;
    }

}
