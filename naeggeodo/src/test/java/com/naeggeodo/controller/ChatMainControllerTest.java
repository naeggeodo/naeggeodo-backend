package com.naeggeodo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naeggeodo.dto.ChatRoomDTO;
import com.naeggeodo.entity.chat.ChatMain;
import com.naeggeodo.entity.chat.ChatState;
import com.naeggeodo.repository.ChatDetailRepository;
import com.naeggeodo.repository.ChatMainRepository;
import com.naeggeodo.repository.TagRepository;
import com.naeggeodo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Sql("classpath:h2/chatMainController.sql")
class ChatMainControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    WebApplicationContext applicationContext;
    @Autowired
    ChatMainRepository chatMainRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    ChatDetailRepository chatDetailRepository;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build()
        ;
    }


    @Test
    @DisplayName("GET /chat-rooms - 빌딩코드 , 카테고리")
    void getChatListTest() throws Exception {

        mockMvc.perform(get("/chat-rooms")
                        .param("category", "PIZZA")
                        .param("buildingCode", "용산구"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].category", equalTo("PIZZA")),
                        jsonPath("$[0].buildingCode", equalTo("용산구"))
                );
    }

    @Test
    @DisplayName("GET /chat-rooms - 빌딩코드만")
    void getChatListTest2() throws Exception {


        MvcResult mvcResult2 = mockMvc.perform(get("/chat-rooms")
                        .param("buildingCode", "용산구"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].buildingCode", equalTo("용산구")),
                        jsonPath("$[1].buildingCode", equalTo("용산구"))
                )
                .andReturn();

    }


    // TODO -> Fail with IllegalStateException: The asyncDispatch CountDownLatch was not set by the TestDispatcherServlet.
    @Test
    @DisplayName("POST /chat-rooms")
    void createChatRoom() throws Exception {
        FileInputStream fileInputStream = new FileInputStream("src/test/resources/testimg.jpg");

        MockMultipartFile file = new MockMultipartFile(
                "file", //name
                "test.jpg", //originalFilename
                "jpg",
                fileInputStream
        );
        MockMultipartFile chat = new MockMultipartFile("chat", "chat", "application/json", createChatRoomDTOToString().getBytes(StandardCharsets.UTF_8));

        MvcResult mvcResult = mockMvc.perform(multipart("/chat-rooms")
                        .file(chat)
                        .file(file)
                        .contentType("multipart/form-data"))
                //.andExpect(request().asyncNotStarted())
                .andExpect(status().isOk())
                .andReturn();

//        mockMvc.perform(asyncDispatch(mvcResult))
//                .andDo(System.out::println);
    }

    @Test
    @DisplayName("POST /chat-rooms/{chatMain_id}/copy")
    void copyChatRoom() throws Exception {
        ChatMain chatMain = chatMainRepository.findAll().get(0);
        Long id = chatMain.getId();


        MvcResult mvcResult = mockMvc.perform(post("/chat-rooms/{chatMain_id}/copy", id)
                        .param("orderTimeType", "ONE_HOUR"))
                .andReturn();

        // TODO 잘 생성되었는지 검증할 방법이 없다!!

    }

    @Test
    @DisplayName("GET /chat-rooms/{chatMain_id}")
    void getChatMain() throws Exception {
        ChatMain chatMain = chatMainRepository.findAll().get(0);
        Long id = chatMain.getId();

        mockMvc.perform(get("/chat-rooms/{chatMain_id}", id)
                        .param("state", "CREATE"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.state", equalTo(ChatState.CREATE.name())),
                        jsonPath("$.id", equalTo(id.intValue()))
                );
    }

    @Test
    @DisplayName("PATCH /chat-rooms/{chatMain_id}")
    void updateRoomState() throws Exception {
        ChatMain chatMain = chatMainRepository.findAll().get(0);
        Long id = chatMain.getId();

        mockMvc.perform(patch("/chat-rooms/{chatMain_id}", id)
                        .param("state", "END"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.state", equalTo(ChatState.END.name())),
                        jsonPath("$.chatMain_id", equalTo(id.intValue()))
                );
    }

    @Test
    @DisplayName("PATCH /chat-rooms/{chatMain_id}")
    void updateTitle() throws Exception {
        List<ChatMain> all = chatMainRepository.findAll();
        Long id = all.get(0).getId();

        MvcResult mvcResult = mockMvc.perform(patch("/chat-rooms/{chatMain_id}", id)
                        .param("title", "제목변경"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.chatMain_id", equalTo(id.intValue())),
                        jsonPath("$.title", equalTo("제목변경"))
                )
                .andReturn();

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

//        List<String> categories  = Arrays.stream(Category.values()).map(Category::name).collect(Collectors.toList());
        mockMvc.perform(get("/categories"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[*].idx", hasItems(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)),
                        jsonPath("$[*].category", hasItems("ALL", "CHICKEN", "PIZZA", "FASTFOOD", "DESSERT", "JAPANESE", "CHINESE", "KOREAN", "SNACKS", "STEW", "WESTERN", "GRILLED_MEAT", "PORK_FEET"))
                );
    }

    @Test
    @DisplayName("GET /chat-rooms/tag")
    void getChatListByTag() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/chat-rooms/tag")
                        .param("keyWord", "tag1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].tags[0]", equalTo("tag1"))
                )
                .andReturn();

    }

    @Test
    @DisplayName("GET /chat-rooms/search")
    void getChatListByKeyWord() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/chat-rooms/search")
                        .param("keyWord", "tag1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].tags[0]", equalTo("tag1"))
                )
                .andReturn();
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

        mockMvc.perform(delete("/chat-rooms/{chatMain_id}", id))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.deleted", equalTo(true)),
                        jsonPath("$.chatMain_id", equalTo(id.intValue()))
                );

    }


    private String createChatRoomDTOToString() throws JsonProcessingException {
        ChatRoomDTO dto = new ChatRoomDTO();
        dto.setBuildingCode("1");
        dto.setAddress("1");
        dto.setCategory("PIZZA");
        dto.setTitle("1");
        dto.setUser_id("test");
        dto.setOrderTimeType("ONE_HOUR");

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(dto);
    }
}