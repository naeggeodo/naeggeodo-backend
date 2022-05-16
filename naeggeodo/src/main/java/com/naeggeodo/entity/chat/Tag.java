package com.naeggeodo.entity.chat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Tag {
	@Id @GeneratedValue
	@Column(name="tag_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chatmain_id")
	private ChatMain chatMain;
	
	private String name;
	
	public static Tag create(ChatMain chatMain,String name) {
		Tag tag = new Tag();
		tag.setChatMain(chatMain);
		tag.setName(name);
		return tag;
	}
	
}
