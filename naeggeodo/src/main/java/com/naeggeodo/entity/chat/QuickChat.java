package com.naeggeodo.entity.chat;

import com.naeggeodo.entity.user.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuickChat{
	@Id @GeneratedValue
	@Column(name = "quickChat_id")
	private Long id;

	@OneToOne(mappedBy = "quickChat",fetch = FetchType.LAZY,cascade = CascadeType.PERSIST)
	private Users user;

	@Column(columnDefinition = "varchar(255) default '반갑습니다 *^ㅡ^*'")
	private String msg1;
	@Column(columnDefinition = "varchar(255) default '주문 완료했습니다! 송금 부탁드려요 *^ㅡ^*'")
	private String msg2;
	@Column(columnDefinition = "varchar(255) default '음식이 도착했어요!'")
	private String msg3;
	@Column(columnDefinition = "varchar(255) default '맛있게 드세요 *^ㅡ^*'")
	private String msg4;
	@Column(columnDefinition = "varchar(255) default '주문내역 확인해주세요!'")
	private String msg5;

	public void setUser(Users user) {
		this.user = user;
		user.setQuickChat(this);
	}

	public static QuickChat create(Users user) {
		return QuickChat.builder().user(user).build();
	}


	public List<String> getMsgList(){
		return Arrays.asList(this.msg1, this.msg2, this.msg3, this.msg4, this.msg5);
	}

	public void updateMsgByList(List<String> list) {
		if(list.size()!=5)  throw new IllegalArgumentException();

		this.msg1 = list.get(0);
		this.msg2 = list.get(1);
		this.msg3 = list.get(2);
		this.msg4 = list.get(3);
		this.msg5 = list.get(4);
	}
}
