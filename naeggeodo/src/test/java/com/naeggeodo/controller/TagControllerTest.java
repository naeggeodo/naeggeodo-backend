package com.naeggeodo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class TagControllerTest {

    @Autowired
    WebApplicationContext applicationContext;
    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();
    }

    @Test
    @DisplayName("GET /chat-rooms/tag/most-wanted")
    @Sql("classpath:h2/tagController.sql")
    void getMostWantedTagList() throws Exception {

        mockMvc.perform(get("/chat-rooms/tag/most-wanted"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.keys()", hasItems("tags")),
                        jsonPath("$.tags.size()", equalTo(10)),
                        jsonPath("$.tags[*].idx", hasItems(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)),
                        jsonPath("$.tags[*].msg", hasItems("감자", "치킨", "주먹밥", "콩순이냉장고", "콜라", "피자", "고구마", "원할머니보쌈", "부대찌개", "사이다"))
                );
    }
}