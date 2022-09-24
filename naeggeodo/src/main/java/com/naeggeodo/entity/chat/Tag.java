package com.naeggeodo.entity.chat;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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
	
	public static Tag create(String name) {
		Tag tag = new Tag();
		tag.setName(name);
		return tag;
	}
	
}
