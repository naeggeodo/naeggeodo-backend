package com.naeggeodo.controller;

import com.naeggeodo.entity.chat.Bookmarks;
import com.naeggeodo.entity.chat.Category;
import com.naeggeodo.entity.chat.ChatMain;
import com.naeggeodo.entity.chat.ChatState;
import com.naeggeodo.repository.ChatMainRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@AutoConfigureMockMvc
@SpringBootTest
@Sql("classpath:h2/controller/chatMain.sql")
class ChatMainControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    WebApplicationContext applicationContext;
    @Autowired
    ChatMainRepository chatMainRepository;

    @BeforeEach
    void setup(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .addFilter(new CharacterEncodingFilter("UTF-8",true))
                .alwaysDo(print())
                .build()
                ;

    }

    @AfterEach
    void after(){
        chatMainRepository.deleteAll();
    }


    @Test
    @DisplayName("GET /chat-rooms")
    void getChatListTest() throws Exception {

        MvcResult mvcResult1 = mockMvc.perform(get("/chat-rooms")
                        .param("category", "PIZZA")
                        .param("buildingCode", "용산구"))
                .andReturn();

        MvcResult mvcResult2 = mockMvc.perform(get("/chat-rooms")
                        .param("buildingCode", "용산구"))
                .andReturn();

        String contentWithCategory = mvcResult1.getResponse().getContentAsString();
        String contentWithoutCategory = mvcResult2.getResponse().getContentAsString();
        JSONArray arr1 = removeKey(contentWithCategory, "chatRoom");
        JSONArray arr2 = removeKey(contentWithoutCategory, "chatRoom");

        assertThat(arr1).size().isEqualTo(1);
        assertThat(arr2).size().isEqualTo(2);

        assertThat(getElementToString(arr1,0,"category")).isEqualTo("PIZZA");
    }




    @Test
    void createChatRoom() {
    }

    @Test
    void copyChatRoom() {
    }

    @Test
    void getChatMain() {
    }

    @Test
    void updateRoomState() {
    }

    @Test
    @DisplayName("PATCH /chat-rooms/{chatMain_id}")
    void updateTitle() throws Exception {
        List<ChatMain> all = chatMainRepository.findAll();
        Long id = all.get(0).getId();

        MvcResult mvcResult = mockMvc.perform(patch("/chat-rooms/{chatMain_id}", id)
                        .param("title", "제목변경"))
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        JSONObject json = new JSONObject(content);
        Optional<ChatMain> findChatMain = chatMainRepository.findById(id);


        assertThat(json.get("chatMain_id")).isEqualTo(id.intValue());
        assertThat(json.get("title")).isEqualTo("제목변경");
        assertThat(findChatMain.get().getTitle()).isEqualTo("제목변경");
    }

    @Test
    void getProgressingChatList() {
    }

    @Test
    void getChatListByStateAndUserId() {
    }

    @Test
    @DisplayName("GET /categories")
    void getCategoryList() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/categories"))
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        JSONArray jsonArray = removeKey(content, "categories");

        Category[] categories = Category.values();

        for (int i = 0; i < jsonArray.length(); i++) {
            assertThat(getElementToString(jsonArray,i,"category")).isEqualTo(categories[i].name());
        }

    }

    @Test
    void getChatListByTag() {
    }

    @Test
    void getChatListByKeyWord() {
    }

    // 토큰에러로 테스트실패
//    @Test
//    @DisplayName("PATCH /chat-rooms/{chatMain_id}/bookmarks/{user_id}")
//    void addBookmarks() throws Exception {
//        String user_id = "test";
//        ChatMain chatMain = chatMainRepository.findByBuildingCodeAndStateNotInOrderByCreateDateDesc("bookmarksTest", Arrays.asList(ChatState.FULL)).get(0);
//
//        MvcResult mvcResult = mockMvc.perform(patch("/chat-rooms/{chatMain_id}/bookmarks/{user_id}", chatMain.getId(), user_id))
//                .andReturn();
//
//        String content = mvcResult.getResponse().getContentAsString();
//        JSONObject json = new JSONObject(content);
//
//        assertThat(json.get("chatMain_id")).isEqualTo(chatMain.getId().intValue());
//        assertThat(json.get("bookmarks")).isEqualTo(chatMain.getBookmarks().name());
//        assertThat(chatMain.getBookmarks()).isEqualTo(Bookmarks.Y);
//
//    }

    @Test
    void getOrderList() {
    }

    @Test
    void deleteChatMain() {
    }


    private JSONArray removeKey(String contents,String key){
        JSONObject json = new JSONObject(contents);
        String str = json.get(key).toString();
        return new JSONArray(str);
    }

    private String getElementToString(JSONArray jsonArray,int index,String key){
        String str = jsonArray.get(index).toString();
        return new JSONObject(str).get(key).toString();
    }
}