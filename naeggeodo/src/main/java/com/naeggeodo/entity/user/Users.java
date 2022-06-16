package com.naeggeodo.entity.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.naeggeodo.entity.chat.QuickChat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.json.JSONObject;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@ToString
public class Users{


	@Id @Column(name="user_id")
	private String id;

	@Column(name="social_id")
	private String socialId;
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

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "quickChat_id")
	private QuickChat quickChat;

	public void updateAddress(String address,String zonecode,String buildingCode) {
		this.address = address;
		this.zonecode = zonecode;
		this.buildingCode = buildingCode;
	}

	public JSONObject AddresstoJSON() {
		JSONObject json = new JSONObject();
		if(address != null){
			json.put("address", address);
		} else {
			json.put("address",JSONObject.NULL);
		}
		if(zonecode != null){
			json.put("zonecode", zonecode);
		} else {
			json.put("zonecode",JSONObject.NULL);
		}
		if(buildingCode != null){
			json.put("buildingCode", buildingCode);
		} else {
			json.put("buildingCode",JSONObject.NULL);
		}
		json.put("user_id", id);
		return json;
	}

}
