package com.naeggeodo.repository;


import com.naeggeodo.dto.AddressDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import com.naeggeodo.entity.user.Users;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;


public interface UserRepository extends JpaRepository<Users, String>{
    @Query("select count(u.id)=2 from Users u where u.id = :id1 or u.id =:id2")
    boolean countForReport(@Param("id1")String id1,@Param("id2")String id2);

    @Query("select new com.naeggeodo.dto.AddressDTO(u.address,u.zonecode,u.buildingCode) from Users u where u.id = :user_id")
    AddressDTO findAddressDTOById(@Param("user_id")String user_id);

    //마이페이지
    @Query(value = "(SELECT count(*) from chat_user cu WHERE user_id = :user_id AND  ban_state = 'ALLOWED') \n" +
            "UNION ALL \n" +
            "(SELECT COUNT(*) FROM deal d WHERE user_id = :user_id)\n" +
            "UNION ALL \n" +
            "(SELECT u.nickname from users u WHERE user_id = :user_id)",nativeQuery = true)
    List<Object> getMyPageCount(@Param("user_id")String user_id);
}
