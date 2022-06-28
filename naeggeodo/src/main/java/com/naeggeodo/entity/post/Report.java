package com.naeggeodo.entity.post;

import com.naeggeodo.entity.user.Users;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Getter @Setter
public class Report {
	@Id @GeneratedValue
	@Column(name = "report_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_id")
	private Users user;


	private String contents;
	private LocalDateTime regDate;

	@Enumerated(EnumType.STRING)
	private ReportType type;
	public static Report create(Users sender,String contents,ReportType type){
		Report report = new Report();
		report.setUser(sender);
		report.setContents(contents);
		report.setRegDate(LocalDateTime.now());
		report.setType(type);
		return report;
	}
}
