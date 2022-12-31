package com.naeggeodo.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class TagRepositoryTest {

    @Autowired
    TagRepository tagRepository;

    @Test
    @DisplayName("사용량 상위 10개 태그 검색 - 사용량순으로 정렬")
    @Sql("classpath:h2/tagRepository.sql")
    void findTop10Tag() {
        List<String> tags = tagRepository.findTop10Tag();

        System.out.println("tags = " + tags);
        assertAll(
                () -> assertThat(tags.get(0)).isEqualTo("감자"),
                () -> assertThat(tags).hasSize(10),
                () -> assertThat(tags).doesNotContain("뽀로로컴퓨터")
        );
    }
    @Test
    @DisplayName("태그 10개 미만시 있는갯수만큼만 가져옴")
    @Sql("classpath:h2/tagRepository2.sql")
    void findTag() {
        List<String> tags = tagRepository.findTop10Tag();

        System.out.println("tags = " + tags);
        assertAll(
                () -> assertThat(tags.get(0)).isEqualTo("감자"),
                () -> assertThat(tags).hasSize(8),
                () -> assertThat(tags.get(7)).isEqualTo("주먹밥")
        );
    }
}