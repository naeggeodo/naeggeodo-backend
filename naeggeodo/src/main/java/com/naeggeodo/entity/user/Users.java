package com.naeggeodo.entity.user;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.naeggeodo.entity.chat.QuickChat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity

public class Users{

	
	@Id @Column(name="user_id")
	private String id;

	
	private String password;
	private String token;
	private String phone;
	
	private String address;
	// �슦�렪踰덊샇
	private String zonecode;
	// buildingcode
	private String buildingCode;
	private String email;
	

	private String nickname;
	@JsonDeserialize(using=LocalDateTimeDeserializer.class)
	private LocalDateTime joindate;
	private LocalDateTime withdrawalDate;
	@Enumerated(EnumType.STRING)
	private Authority authority;
	
	private String imgpath;
	
	@OneToOne(mappedBy = "user")
	private QuickChat quickChat;
	
	public void updateAddress(String address,String zonecode,String buildingCode) {
		this.address = address;
		this.zonecode = zonecode;
		this.buildingCode = buildingCode;
	}

	public JSONObject AddresstoJSON() {
		JSONObject json = new JSONObject();
		json.put("address", address);
		json.put("zonecode", zonecode);
		json.put("buildingCode", buildingCode);
		json.put("id", id);
		return json;
	}

}
