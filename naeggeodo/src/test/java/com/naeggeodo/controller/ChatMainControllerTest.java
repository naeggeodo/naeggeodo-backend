package com.naeggeodo.controller;

import com.naeggeodo.entity.chat.Category;
import com.naeggeodo.entity.chat.ChatMain;
import com.naeggeodo.entity.chat.ChatState;
import com.naeggeodo.repository.ChatMainRepository;
import com.naeggeodo.repository.UserRepository;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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
    @Autowired
    UserRepository userRepository;

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
        userRepository.deleteAll();
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
    @DisplayName("GET /chat-rooms/{chatMain_id}")
    void getChatMain() throws Exception {
        ChatMain chatMain = chatMainRepository.findAll().get(0);
        Long id = chatMain.getId();

        MvcResult mvcResult = mockMvc.perform(get("/chat-rooms/{chatMain_id}", id)
                        .param("state", "CREATE"))
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        JSONObject jsonObject = new JSONObject(content);

        assertThat(jsonObject.get("state")).isEqualTo(ChatState.CREATE.name());
        assertThat(jsonObject.get("id")).isEqualTo(id.intValue());
    }

    @Test
    @DisplayName("PATCH /chat-rooms/{chatMain_id}")
    void updateRoomState() throws Exception {
        ChatMain chatMain = chatMainRepository.findAll().get(0);
        Long id = chatMain.getId();

        MvcResult mvcResult = mockMvc.perform(patch("/chat-rooms/{chatMain_id}", id)
                        .param("state", "END"))
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        JSONObject jsonObject = new JSONObject(content);

        assertThat(jsonObject.get("state")).isEqualTo(ChatState.END.name());
        assertThat(jsonObject.get("chatMain_id")).isEqualTo(id.intValue());
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
//    user_id 가 path에 들어가면 토큰검증해서 일단 보류
//    @Test
//    void getProgressingChatList() {
//    }
//
//    @Test
//    void getChatListByStateAndUserId() {
//    }

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

    // 인터셉터 토큰에러로 테스트실패
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
//
//    @Test
//    void getOrderList() {
//    }

    @Test
    @DisplayName("DELETE /chat-rooms/{chatMain_id}")
    void deleteChatMain() throws Exception {
        List<ChatMain> list = chatMainRepository.findByBuildingCodeAndStateNotInOrderByCreateDateDesc("deleteTest", ChatState.searchableList);

        Long id = list.get(0).getId();

        MvcResult mvcResult = mockMvc.perform(delete("/chat-rooms/{chatMain_id}", id))
                                     .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        JSONObject jsonObject = new JSONObject(content);

        assertThat(jsonObject.get("deleted")).isEqualTo(true);
        assertThat(jsonObject.get("chatMain_id")).isEqualTo(id.intValue());
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