package com.naeggeodo.entity.post;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class FAQ {
	@Id @GeneratedValue
	@Column(name = "faq_id")
	private Long id;
	
	private String title;
	private String contents;
	private LocalDateTime regDate;
	@Enumerated(EnumType.STRING)
	private State_NoticeFAQ state; 
	private LocalDateTime delDate;
	private String imgpath;
	
}
