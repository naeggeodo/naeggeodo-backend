package com.naeggeodo.repository;

import com.naeggeodo.entity.chat.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long>{

	@Query(value = "SELECT t.name from tag t group by t.name order by COUNT(t.name) desc limit 10",nativeQuery = true)
	List<String> findTop10Tag();
}
