package com.naeggeodo.interfaces;

import org.json.JSONObject;

public interface JSONConverter {
	JSONObject toJSON() throws Exception;
	JSONObject toJSONIgnoringCurrentCount() throws Exception;
}
