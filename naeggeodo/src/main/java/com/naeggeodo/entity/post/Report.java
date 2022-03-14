package com.naeggeodo.entity.post;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.naeggeodo.entity.user.Users;

import lombok.Getter;
import lombok.Setter;

@Entity @Getter @Setter
public class Report {
	@Id @GeneratedValue
	@Column(name = "report_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private Users user;
	
	private String title;
	private String contents;
	private LocalDateTime regDate;
	@Enumerated(EnumType.STRING)
	private State_ReportQNA state;
}
