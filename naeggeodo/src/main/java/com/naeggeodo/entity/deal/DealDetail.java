package com.naeggeodo.entity.deal;

import java.time.LocalDateTime;

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
public class DealDetail {
	@Id @GeneratedValue
	@Column(name ="dealDetail_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="deal_id")
	private DealHistory dealHistory;
	
	private String dealdetail_type;
	
	private LocalDateTime dealdetail_date;
}
