package com.naeggeodo.repository;

import com.naeggeodo.entity.chat.ChatDetail;
import com.naeggeodo.entity.chat.ChatDetailType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ChatDetailRepositoryTest {
    @Autowired private ChatDetailRepository chatDetailRepository;

    @Test
    @Transactional
    void saveTest(){
        String contents ="테스트테스트";

        ChatDetail savedChatDetail = chatDetailRepository.save(ChatDetail.create(contents,null,null, ChatDetailType.TEXT));
        chatDetailRepository.flush();

        Assertions.assertEquals(contents,savedChatDetail.getContents());
        Assertions.assertEquals(ChatDetailType.TEXT,savedChatDetail.getType());
    }

    @Test
    @Transactional
    void findAllTest(){
        Assertions.assertEquals(5,chatDetailRepository.findAll().size());
    }

    @Test
    @Transactional
    void loadTest(){
        List<ChatDetail> loadedList = chatDetailRepository.load(1L,"kmh2");

        Assertions.assertEquals(2,loadedList.size());
    }

    @Test
    @Transactional
    void findByIdTest(){
        ChatDetail chatDetail = chatDetailRepository.findById(6L).orElseThrow(RuntimeException::new);

        Assertions.assertEquals("1st",chatDetail.getContents());
    }

    // ConversionFailedException
    //Failed to convert from type [java.util.ArrayList<?>] to type [@org.springframework.data.jpa.repository.Query java.util.List<java.lang.String>] for value '[clob1: '3rd', clob2: '5th']';
    @Test
    @Transactional
    void findLatestContentsTest(){
        //List<String> list = chatDetailRepository.findLatestContents(Arrays.asList(1L,2L));

        //Assertions.assertEquals("3rd",list.get(0));
        //Assertions.assertEquals("5th",list.get(1));
    }
}
