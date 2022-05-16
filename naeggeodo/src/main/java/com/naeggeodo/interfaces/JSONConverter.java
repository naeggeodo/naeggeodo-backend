package com.naeggeodo.interfaces;

import org.json.JSONObject;

public interface JSONConverter {
	public JSONObject toJSON() throws Exception;
	public JSONObject toJSONIgnoringCurrentCount() throws Exception;
}
