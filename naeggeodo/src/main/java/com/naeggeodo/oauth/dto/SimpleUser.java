package com.naeggeodo.oauth.dto;

import com.naeggeodo.entity.user.Authority;
import lombok.Data;

@Data
public class SimpleUser {
	private String Id;
	private String address;
	private Authority authority;
	
	public SimpleUser(String userId, String address, Authority authority) {
		super();
		this.Id = userId;
		this.address = address;
		this.authority = authority;
	}
	
}
