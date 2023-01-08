package com.naeggeodo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naeggeodo.dto.AddressDTO;
import com.naeggeodo.exception.CustomHttpException;
import com.naeggeodo.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Sql("classpath:h2/userController.sql")
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    WebApplicationContext applicationContext;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build()
        ;

    }

    @Test
    @DisplayName("PATCH /user/{user_id}/address")
    void updateAddress() throws Exception {
        String userId = "user0";
        AddressDTO addressDTO = new AddressDTO("address1", "zonecode1", "buildingCode1");
        String body = objectMapper.writeValueAsString(addressDTO);

        mockMvc.perform(patch("/user/{user_id}/address", userId)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.success", equalTo(true)),
                        jsonPath("$.data").isMap(),
                        jsonPath("$.status", equalTo(200)),
                        jsonPath("$.data.address", equalTo("address1")),
                        jsonPath("$.data.zonecode", equalTo("zonecode1")),
                        jsonPath("$.data.buildingCode", equalTo("buildingCode1")),
                        jsonPath("$.data.user_id", equalTo("user0"))
                );
    }

    @Test
    @DisplayName("PATCH /user/{user_id}/address - 유저 없을때")
    void updateAddress2() throws Exception {
        String userId = "user1";
        AddressDTO addressDTO = new AddressDTO("a", "b", "c");
        String body = objectMapper.writeValueAsString(addressDTO);

        mockMvc.perform(patch("/user/{user_id}/address", userId)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                result -> assertThat(
                        ((CustomHttpException) result.getResolvedException()).getErrorCode()
                ).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND),
                status().isNotFound()
        );
    }

    @Test
    @DisplayName("GET /user/{user_id}/address")
    void getAddress() throws Exception {
        String userId = "user0";
        mockMvc.perform(get("/user/{user_id}/address", userId))
                .andExpectAll(
                        status().isOk(),
                        status().isOk(),
                        jsonPath("$.success", equalTo(true)),
                        jsonPath("$.data").isMap(),
                        jsonPath("$.status", equalTo(200)),
                        jsonPath("$.data.address", equalTo("address")),
                        jsonPath("$.data.zonecode", equalTo("zonecode")),
                        jsonPath("$.data.buildingCode", equalTo("building_code")),
                        jsonPath("$.data.user_id", equalTo("user0"))
                )
        ;
    }

    @Test
    @DisplayName("GET /user/{user_id}/address - 유저 없을때")
    void getAddress2() throws Exception {
        String userId = "user1";
        mockMvc.perform(get("/user/{user_id}/address", userId))
                .andExpectAll(
                        status().isNotFound(),
                        result -> assertThat(
                                ((CustomHttpException) result.getResolvedException()).getErrorCode()
                        ).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND)
                );
    }


    //
    @Test
    @DisplayName("GET /user/{user_id}/mypage")
    void getMyPageDate() throws Exception {
        String userId = "user0";

        mockMvc.perform(get("/user/{user_id}/mypage", userId))
                .andExpectAll(
                        status().isOk(),
                        status().isOk(),
                        jsonPath("$.success", equalTo(true)),
                        jsonPath("$.data").isMap(),
                        jsonPath("$.status", equalTo(200)),
                        jsonPath("$.data.participatingChatCount", equalTo(1)),
                        jsonPath("$.data.myOrdersCount", equalTo(1)),
                        jsonPath("$.data.nickname", equalTo("도봉산-왕주먹"))
                );
    }

    @Test
    @DisplayName("GET /user/{user_id}/mypage - 유저가 없을때")
    void getMyPageDate2() throws Exception {
        String userId = "user1";

        mockMvc.perform(get("/user/{user_id}/mypage", userId))
                .andExpectAll(
                        status().isNotFound(),
                        result -> assertThat(
                                ((CustomHttpException) result.getResolvedException()).getErrorCode()
                        ).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND)

                );
    }

    @Test
    @DisplayName("GET /user/{user_id}/quick-chatting")
    void getQuickChat() throws Exception {
        String userId = "user0";

        mockMvc.perform(get("/user/{user_id}/quick-chatting", userId))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.success", equalTo(true)),
                        jsonPath("$.data").isArray(),
                        jsonPath("$.status", equalTo(200)),
                        jsonPath("$.data.size()", equalTo(5)),
                        jsonPath("$.data", hasItems("반갑습니다 *^ㅡ^*", "주문 완료했습니다! 송금 부탁드려요 *^ㅡ^*", "음식이 도착했어요!", "맛있게 드세요 *^ㅡ^*", "주문내역 확인해주세요!"))
                );
    }

    @Test
    @DisplayName("GET /user/{user_id}/quick-chatting - 유저 없을때")
    void getQuickChat2() throws Exception {
        String userId = "user1";

        mockMvc.perform(get("/user/{user_id}/quick-chatting", userId))
                .andExpectAll(
                        status().isNotFound(),
                        result -> assertThat(
                                ((CustomHttpException) result.getResolvedException()).getErrorCode()
                        ).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND)
                );
    }

    @Test
    @DisplayName("PATCH /user/{user_id}/quick-chatting")
    void updateQuickChat() throws Exception {
        String userId = "user0";

        List<String> list = Arrays.asList("1", "2", "3", "4", "5");
        String s = objectMapper.writeValueAsString(list);


        mockMvc.perform(patch("/user/{user_id}/quick-chatting", userId)
                        .content(s)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.success", equalTo(true)),
                        jsonPath("$.data").isArray(),
                        jsonPath("$.status", equalTo(200)),
                        jsonPath("$.data.size()", equalTo(5)),
                        jsonPath("$.data", hasItems("1", "2", "3", "4", "5"))
                );
    }

    @Test
    @DisplayName("PATCH /user/{user_id}/quick-chatting - 유저 없을때")
    void updateQuickCha2t() throws Exception {
        String userId = "user1";

        List<String> list = Arrays.asList("1", "2", "3", "4", "5");
        String s = objectMapper.writeValueAsString(list);


        mockMvc.perform(patch("/user/{user_id}/quick-chatting", userId)
                        .content(s)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isNotFound(),
                        result -> assertThat(
                                ((CustomHttpException) result.getResolvedException()).getErrorCode()
                        ).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND)
                );
    }

    @Test
    @DisplayName("GET /user/{user_id}/nickname")
    void getNickname() throws Exception {
        String userId = "user0";

        mockMvc.perform(get("/user/{user_id}/nickname", userId))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.success", equalTo(true)),
                        jsonPath("$.data").isMap(),
                        jsonPath("$.status", equalTo(200)),
                        jsonPath("$.data.keys()", hasItems("nickname", "user_id")),
                        jsonPath("$.data.size()", equalTo(2)),
                        jsonPath("$.data.nickname", equalTo("도봉산-왕주먹")),
                        jsonPath("$.data.user_id", equalTo("user0"))
                )
        ;
    }

    @Test
    @DisplayName("GET /user/{user_id}/nickname - 유저없을때")
    void getNickname2() throws Exception {
        String userId = "user1";

        mockMvc.perform(get("/user/{user_id}/nickname", userId))
                .andExpectAll(
                        status().isNotFound(),
                        result -> assertThat(
                                ((CustomHttpException) result.getResolvedException()).getErrorCode()
                        ).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND)
                );
    }

    @Test
    @DisplayName("PATCH /user/{user_id}/nickname")
    void updateNickname() throws Exception {
        String userId = "user0";

        mockMvc.perform(patch("/user/{user_id}/nickname", userId).param("value", "도봉산-호랑이"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.success", equalTo(true)),
                        jsonPath("$.data").isMap(),
                        jsonPath("$.status", equalTo(200)),
                        jsonPath("$.data.nickname", equalTo("도봉산-호랑이")),
                        jsonPath("$.data.user_id", equalTo("user0"))
                );
    }

    @Test
    @DisplayName("PATCH /user/{user_id}/nickname - 유저없을때")
    void updateNickname2() throws Exception {
        String userId = "user1";

        mockMvc.perform(patch("/user/{user_id}/nickname", userId).param("value", "exception"))
                .andExpectAll(
                        status().isNotFound(),
                        result -> assertThat(
                                ((CustomHttpException) result.getResolvedException()).getErrorCode()
                        ).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND)
                );
    }
}
