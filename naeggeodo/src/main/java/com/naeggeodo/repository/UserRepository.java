package com.naeggeodo.repository;


import com.naeggeodo.dto.AddressDTO;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.naeggeodo.entity.user.Authority;
import com.naeggeodo.entity.user.Users;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;


public interface UserRepository extends JpaRepository<Users, String>{
    @Query("select count(u.id)=2 from Users u where u.id = :id1 or u.id =:id2")
    boolean countForReport(@Param("id1")String id1,@Param("id2")String id2);

    @EntityGraph(attributePaths = "quickChat")
    @Query("select u from Users u where u.id = :id")
    Optional<Users> findQuickChatEntityGraph(@Param("id")String id);

    @Query("select new com.naeggeodo.dto.AddressDTO(u.address,u.zonecode,u.buildingCode) from Users u where u.id = :user_id")
    AddressDTO findAddressDTOById(@Param("user_id")String user_id);

    //마이페이지
    @Query(value = "(SELECT count(*) FROM chat_user cu \n" +
            "inner join chat_main cm on cm.chatmain_id = cu.chatmain_id \n" +
            "WHERE cu.user_id = :user_id \n" +
            "AND cu.ban_state = 'ALLOWED' \n" +
            "AND cm.state not in ('END','INCOMPLETE')\n" +
            "UNION ALL \n" +
            "(SELECT COUNT(*) FROM deal d WHERE user_id = :user_id)\n" +
            "UNION ALL \n" +
            "(SELECT u.nickname from users u WHERE user_id = :user_id)",nativeQuery = true)
    List<Object> getMyPageCount(@Param("user_id")String user_id);

    Users findBySocialIdAndAuthority(String socialId, Authority authority);
}
