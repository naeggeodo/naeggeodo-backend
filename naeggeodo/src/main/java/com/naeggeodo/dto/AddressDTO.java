package com.naeggeodo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
	private String address;
	private String zonecode;
	private String buildingCode;

	public JSONObject toJSON(){
		JSONObject json = new JSONObject();
		json.put("address",address);
		json.put("zonecode",zonecode);
		json.put("buildingCode",buildingCode);
		return json;
	}
}
