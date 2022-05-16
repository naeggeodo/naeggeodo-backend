package com.naeggeodo.interfaces;

import org.json.JSONObject;

public abstract class JSONConverterAdapter implements JSONConverter{

	@Override
	public JSONObject toJSON() throws Exception {return null;}

	@Override
	public JSONObject toJSONIgnoringCurrentCount() throws Exception {return null;}
	

}
