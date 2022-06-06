package com.naeggeodo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.naeggeodo.entity.chat.Tag;

public interface TagRepository extends JpaRepository<Tag, Long>{

	@Query(value = "SELECT t.name from tag t group by t.name order by COUNT(t.name) desc limit 10",nativeQuery = true)
	List<String> findTop10Tag();
//	@Query(value = "SELECT t.name from Tag t group by t.name order by COUNT(t.name) desc")
//	public List<String> findTop10Tag(Pageable pageable);
}