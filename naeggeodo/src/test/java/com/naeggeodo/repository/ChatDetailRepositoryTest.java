package com.naeggeodo.repository;

import com.naeggeodo.entity.chat.ChatDetail;
import com.naeggeodo.entity.chat.ChatMain;
import com.naeggeodo.entity.chat.ChatState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Sql("classpath:h2/chatDetailRepository.sql")
public class ChatDetailRepositoryTest {
    @Autowired
    private ChatDetailRepository chatDetailRepository;
    @Autowired
    private ChatMainRepository chatMainRepository;

    @Test
    @DisplayName("채팅방 입장시 기존 입장 시간 기준으로 불러오기 기능")
    void loadTest(){
        Long chatMainId = chatDetailRepository.findAll().get(0).getChatmain().getId();
        String userId = "user1";

        List<ChatDetail> list = chatDetailRepository.load(chatMainId, userId);

        assertThat(list).size().isEqualTo(2);
        assertRegDateIsSortedAsc(list);
    }

    // org.springframework.core.convert.ConverterNotFoundException: No converter found capable of converting from type
    @Test
    @DisplayName("내꺼톡 - 마지막 채팅 내용 조회")
    void findLatestContentsTest(){
        List<ChatMain> chatMainList = chatMainRepository.findByUserIdInChatUser("user0", ChatState.insearchableList);
        List<Long> idList = chatMainList.stream().map(ChatMain::getId).collect(Collectors.toList());
        List<String> latestContents = chatDetailRepository.findLatestContents(idList);

        System.out.println(latestContents);
    }

    private void assertRegDateIsSortedAsc(List<ChatDetail> list){
        assertThat(list).isSortedAccordingTo(
                (c1,c2) -> c1.getRegDate().compareTo(c2.getRegDate())
        );
    }



}
