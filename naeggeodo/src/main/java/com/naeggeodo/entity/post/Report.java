package com.naeggeodo.entity.post;

import java.time.LocalDateTime;

import javax.persistence.*;

import com.naeggeodo.entity.user.Users;

import lombok.Getter;
import lombok.Setter;

@Entity @Getter @Setter
public class Report {
	@Id @GeneratedValue
	@Column(name = "report_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_id")
	private Users user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="reported_user_id")
	private Users reported_user;
	
	private String contents;
	private LocalDateTime regDate;
	@Enumerated(EnumType.STRING)
	private State_ReportQNA state;

	public static Report create(Users sender,Users target,String contents){
		Report report = new Report();
		report.setUser(sender);
		report.setReported_user(target);
		report.setRegDate(LocalDateTime.now());
		report.setState(State_ReportQNA.PROCESSED);
		report.setContents(contents);
		return report;
	}
}
