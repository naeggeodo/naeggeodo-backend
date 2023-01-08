package com.naeggeodo.util;

import com.naeggeodo.interfaces.JSONConverter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;


public class MyUtility {

    // convert List<Entity> -> JSONObject using JSONConverter.toJSON()
    public static <T extends JSONConverter> JSONObject convertListToJSONobj(List<T> list, String key) throws Exception {
        JSONObject jsonResult = new JSONObject();
        JSONArray arr_json = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            JSONConverter toJson = list.get(i);
            JSONObject json = toJson.toJSON();
            json.put("idx", i);
            arr_json.put(json);
        }

        jsonResult.put(key, arr_json);

        return jsonResult;
    }
}
