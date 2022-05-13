package com.naeggeodo.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.naeggeodo.entity.chat.Tag;

public interface TagRepository extends JpaRepository<Tag, Long>{

	@Query("SELECT t.name from Tag t group by t.name order by COUNT(t.name) desc")
	public List<String> findTop10Tag(Pageable pageable);
}
