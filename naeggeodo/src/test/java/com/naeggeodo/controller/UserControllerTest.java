package com.naeggeodo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naeggeodo.dto.AddressDTO;
import com.naeggeodo.exception.CustomHttpException;
import com.naeggeodo.exception.ErrorCode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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

        MvcResult mvcResult = mockMvc.perform(patch("/user/{user_id}/address", userId)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        int status = response.getStatus();
        JSONObject jsonObject = new JSONObject(response.getContentAsString());

        assertAll(
                () -> assertThat(status).isEqualTo(200),
                () -> assertThat(jsonObject.get("address")).isEqualTo("address1"),
                () -> assertThat(jsonObject.get("zonecode")).isEqualTo("zonecode1"),
                () -> assertThat(jsonObject.get("buildingCode")).isEqualTo("buildingCode1"),
                () -> assertThat(jsonObject.get("buildingCode")).isEqualTo("user0")
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
        ).andExpect(
                result -> assertAll(
                        () -> assertThat(
                                ((CustomHttpException) result.getResolvedException()).getErrorCode()
                        ).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND),
                        () -> assertThat(result.getResponse().getStatus()).isEqualTo(404)
                )
        );
    }

    @Test
    @DisplayName("GET /user/{user_id}/address")
    void getAddress() throws Exception {
        String userId = "user0";
        MvcResult mvcResult = mockMvc.perform(get("/user/{user_id}/address", userId))
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        JSONObject jsonObject = new JSONObject(response.getContentAsString());


        assertAll(
                () -> assertThat(response.getStatus()).isEqualTo(200),
                () -> assertThat(jsonObject.get("address")).isEqualTo("address"),
                () -> assertThat(jsonObject.get("zonecode")).isEqualTo("zonecode"),
                () -> assertThat(jsonObject.get("buildingCode")).isEqualTo("building_code"),
                () -> assertThat(jsonObject.get("user_id")).isEqualTo("user0")
        );
    }

    @Test
    @DisplayName("GET /user/{user_id}/address - 유저 없을때")
    void getAddress2() throws Exception {
        String userId = "user1";
        MvcResult mvcResult = mockMvc.perform(get("/user/{user_id}/address", userId))
                .andExpect(result ->
                        assertAll(
                                () -> assertThat(result.getResponse().getStatus()).isEqualTo(404),
                                () -> assertThat(
                                        ((CustomHttpException) result.getResolvedException()).getErrorCode()
                                ).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND)
                        )
                )
                .andReturn();
    }


    //
    @Test
    @DisplayName("마이페이지 - 성공케이스")
    void getMyPageDate() throws Exception {
        String userId = "user0";

        MvcResult mvcResult = mockMvc.perform(get("/user/{user_id}/mypage", userId))
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject jsonObject = new JSONObject(contentAsString);

        assertAll(
                () -> assertThat(jsonObject.get("participatingChatCount")).isEqualTo(1),
                () -> assertThat(jsonObject.get("myOrdersCount")).isEqualTo(1),
                () -> assertThat(jsonObject.get("nickname")).isEqualTo("도봉산-왕주먹")
        );
    }

    @Test
    @DisplayName("마이페이지 - 유저가 없을때")
    void getMyPageDate2() throws Exception {
        String userId = "user1";

        mockMvc.perform(get("/user/{user_id}/mypage", userId))
                .andExpect(
                        result -> assertAll(
                                () -> assertThat(result.getResolvedException()).isInstanceOf(CustomHttpException.class),
                                () -> assertThat(
                                        ((CustomHttpException) result.getResolvedException()).getErrorCode()
                                ).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND)
                        )
                )
                .andExpect(
                        result -> assertThat(result.getResponse().getStatus()).isEqualTo(404)
                );
    }

    @Test
    void getQuickChat() {
    }

    @Test
    void updateQuickChat() {
    }

    @Test
    void getNickname() {
    }

    @Test
    void updateNickname() {
    }
}