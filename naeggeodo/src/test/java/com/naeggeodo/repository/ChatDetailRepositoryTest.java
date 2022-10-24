package com.naeggeodo.repository;

import com.naeggeodo.entity.chat.ChatDetail;
import com.naeggeodo.entity.chat.ChatDetailType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ChatDetailRepositoryTest {
    @Autowired
    private ChatDetailRepository chatDetailRepository;

    @Test
    void saveTest(){
        String contents ="테스트테스트";

        ChatDetail savedChatDetail = chatDetailRepository.save(ChatDetail.create(contents,null,null, ChatDetailType.TEXT));
        chatDetailRepository.flush();

        assertThat(savedChatDetail.getContents()).isEqualTo(contents);
        assertThat(savedChatDetail.getType()).isEqualTo(ChatDetailType.TEXT);
    }

}
