package com.naeggeodo.oauth.dto;

import com.naeggeodo.entity.user.Authority;


import lombok.Data;

@Data
public class SimpleUser {
	private String Id;
	private String address;
	private Authority authority;

	// 22.06.12 수정 -김민혁
	private String buildingCode;
	
	public SimpleUser(String userId, String address, String buildingCode,Authority authority) {
		super();
		this.Id = userId;
		this.address = address;
		this.buildingCode = buildingCode;
		this.authority = authority;
	}
	
}
