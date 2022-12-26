package com.naeggeodo.repository;


import com.naeggeodo.dto.MypageDTO;
import com.naeggeodo.entity.user.Authority;
import com.naeggeodo.entity.user.Users;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<Users, String>{
    @EntityGraph(attributePaths = "quickChat")
    @Query("select u from Users u where u.id = :id")
    Optional<Users> findQuickChatEntityGraph(@Param("id")String id);

    //마이페이지
    @Query(value = "(SELECT count(*) FROM chat_user cu \n" +
            "inner join chat_main cm on cm.chatmain_id = cu.chatmain_id \n" +
            "WHERE cu.user_id = :user_id \n" +
            "AND cu.ban_state = 'ALLOWED' \n" +
            "AND cm.state not in ('END','INCOMPLETE'))\n" +
            "UNION ALL \n" +
            "(SELECT COUNT(*) FROM deal d WHERE user_id = :user_id)\n" +
            "UNION ALL \n" +
            "(SELECT u.nickname from users u WHERE user_id = :user_id)",nativeQuery = true)
    List<Object> getMyPageCount(@Param("user_id")String user_id);

    @Query(value = "select  participatingChatCount ,myOrdersCount ,u.nickname\n" +
            "from (\n" +
            "SELECT count(*) as participatingChatCount\n" +
            "FROM chat_user cu \n" +
            "inner join chat_main cm \n" +
            "on cm.chatmain_id = cu.chatmain_id\n" +
            "WHERE cu.user_id = :user_id\n" +
            "AND cu.ban_state = 'ALLOWED' \n" +
            "AND cm.state not in ('END','INCOMPLETE')\n" +
            ") a , (\n" +
            "SELECT COUNT(*)  as myOrdersCount\n" +
            "FROM deal d \n" +
            "WHERE user_id = :user_id\n" +
            ") b , users u\n" +
            "where u.user_id = :user_id",nativeQuery = true)
    MypageDTO getMyPageData(@Param("user_id")String user_id);
    Users findBySocialIdAndAuthority(String socialId, Authority authority);
}
