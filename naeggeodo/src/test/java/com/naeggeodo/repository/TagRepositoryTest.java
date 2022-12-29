package com.naeggeodo.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql("classpath:h2/tagRepository.sql")
class TagRepositoryTest {

    @Autowired
    TagRepository tagRepository;

    @Test
    @DisplayName("사용량 상위 10개 태그 검색 - 사용량순으로 정렬")
    void findTop10Tag() {
        List<String> tags = tagRepository.findTop10Tag();

        System.out.println("tags = " + tags);
        assertAll(
                () -> assertThat(tags.get(0)).isEqualTo("감자"),
                () -> assertThat(tags).hasSize(10),
                () -> assertThat(tags).doesNotContain("뽀로로컴퓨터")
        );
    }
}