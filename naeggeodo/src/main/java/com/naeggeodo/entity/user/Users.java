package com.naeggeodo.entity.user;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.naeggeodo.entity.chat.ChatDetail;
import com.naeggeodo.entity.chat.ChatMain;
import com.naeggeodo.entity.deal.DealHistory;
import com.naeggeodo.entity.post.Notice;
import com.naeggeodo.entity.post.Qna;
import com.naeggeodo.entity.post.Report;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@NoArgsConstructor
@Entity
public class Users {

	/*
	 * @Builder public Users(String id, String phone, String nickname) { this.id =
	 * id; this.phone = phone; this.nickname = nickname; }
	 */
	
	
	@Id @Column(name="user_id")
	private String id;
	
//	private String password;
//	private String token;
	private String phone;
	private String email;
	private String addr;
	private String nickname;
	@JsonDeserialize(using=LocalDateTimeDeserializer.class)
	private LocalDateTime joindate;
	private LocalDateTime withdrawalDate;
	@Enumerated(EnumType.STRING)
	private Authority authority;
	
	@OneToMany(mappedBy = "user")
	private List<DealHistory> dealHistory;
	@OneToMany(mappedBy = "user")
	private List<ChatMain> chatMain;
	@OneToMany(mappedBy = "user")
	private List<Notice> notice;
	@OneToMany(mappedBy = "user")
	private List<Qna> qna;
	@OneToMany(mappedBy = "user")
	private List<Report> report;
	
}
