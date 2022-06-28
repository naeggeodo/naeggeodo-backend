package com.naeggeodo.repository;

import com.naeggeodo.entity.chat.ChatDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatDetailRepository extends JpaRepository<ChatDetail, Long>{

	@Query("SELECT cd FROM ChatDetail cd join fetch cd.user WHERE cd.chatmain.id = :chatMain_id AND cd.regDate >= (SELECT cu.enterDate FROM ChatUser cu WHERE cu.chatMain.id = :chatMain_id AND cu.user.id =:user_id)" +
			"order by cd.regDate asc")
	List<ChatDetail> load(@Param("chatMain_id")Long chatMain_id, @Param("user_id") String user_id);

	@Query(value = "select c.contents\n" +
			"FROM (\n" +
			"select *\n" +
			"from chat_detail \n" +
			"WHERE (chatmain_id,reg_date) in (\n" +
			"SELECT chatmain_id,MAX(reg_date) as reg_date\n" +
			"from chat_detail WHERE chatmain_id in :idList AND `type` in ('CREATED','TEXT') group by chatmain_id\n" +
			")\n" +
			"order by reg_date desc\n" +
			") c\n" +
			"group by c.chatmain_id",nativeQuery = true)
	List<String> findLatestContents(@Param("idList")List<Long> idList);
}
