package com.naeggeodo.util;

import com.naeggeodo.entity.chat.Category;
import com.naeggeodo.interfaces.JSONConverter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MyUtility {

	// convert List<Entity> -> JSONObject using JSONConverter.toJSON()
	public static <T extends JSONConverter> JSONObject convertListToJSONobj(List<T> list,String key) throws Exception {
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

	// 참여중인 채팅방 에서만 사용
	public static <T extends JSONConverter> JSONObject convertListToJSONobj(List<T> list,List<String> msgList,String key) throws Exception {
		JSONObject jsonResult = new JSONObject();
		JSONArray arr_json = new JSONArray();
		for (int i = 0; i < list.size(); i++) {
			JSONConverter toJson = list.get(i);
			JSONObject json = toJson.toJSON();
			json.put("idx", i);
			json.put("latestMessage",msgList.get(i));
			arr_json.put(json);
		}

		jsonResult.put(key, arr_json);

		return jsonResult;
	}
	
	public static <T extends JSONConverter> JSONObject convertListToJSONobjIgnoringCurrentCount(List<T> list,String key) throws Exception {
		JSONObject jsonResult = new JSONObject();
		JSONArray arr_json = new JSONArray();
		for (int i = 0; i < list.size(); i++) {
			JSONConverter toJson = list.get(i);
			JSONObject json = toJson.toJSONIgnoringCurrentCount();
			json.put("idx", i);
			arr_json.put(json);
		}
		
		jsonResult.put(key, arr_json);
		
		return jsonResult;
	}
	
	public static JSONObject convertStringListToJSONObject(List<String> list,String key) {
		JSONObject jsonResult = new JSONObject();
		JSONArray arr_json = new JSONArray();
		for (int i = 0; i < list.size(); i++) {
			JSONObject json = new JSONObject();
			json.put("idx", i);
			json.put("msg", list.get(i)!=null?list.get(i):JSONObject.NULL);
			arr_json.put(json);
		}
		jsonResult.put(key, arr_json);
		
		return jsonResult;
	}
	
	public static JSONObject convertCategoryToJSONobj(String key){
		JSONObject jsonResult = new JSONObject();
		JSONArray arr_json = new JSONArray();
		
		for (int i = 0; i < Category.values().length; i++) {
			JSONObject json = new JSONObject();
			json.put("idx", i);
			json.put("category", Category.values()[i].name());
			arr_json.put(json);
		}
		jsonResult.put(key, arr_json);
		return jsonResult;
	}
	
	public static List<String> convertQuickChatJSONArrayToStringList(JSONArray arr_json) {
		List<String> list_result = new ArrayList<>();
		for (Object obj : arr_json) {
			list_result.add(new JSONObject(obj.toString()).getString("msg"));
		}
		
		return list_result;
	}
}
