package com.naeggeodo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
	@NotBlank
	private String address;
	@NotBlank
	private String zonecode;
	@NotBlank
	private String buildingCode;

	public JSONObject toJSON(){
		JSONObject json = new JSONObject();
		json.put("address",address);
		json.put("zonecode",zonecode);
		json.put("buildingCode",buildingCode);
		return json;
	}
}
