package com.naeggeodo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.naeggeodo.entity.chat.ChatDetail;

public interface ChatDetailRepository extends JpaRepository<ChatDetail, Long>{
	
	@Query("SELECT cd FROM ChatDetail cd WHERE cd.chatmain.id = :chatMain_id AND cd.regDate >= (SELECT cu.enterDate FROM ChatUser cu WHERE cu.chatMain.id = :chatMain_id AND cu.user.id =:user_id)")
	public List<ChatDetail> load(@Param("chatMain_id")Long chatMain_id, @Param("user_id") String user_id);
}
