//package com.naeggeodo.listener;
//
//import com.naeggeodo.entity.chat.ChatMain;
//import com.naeggeodo.repository.ChatMainRepository;
//import lombok.NoArgsConstructor;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.persistence.PostPersist;
//
//@Component
//public class ChatMainListener {
//    @Autowired @Lazy
//    private ChatMainRepository chatMainRepository;
//    @PostPersist
//    @Transactional
//    public void postPersist(ChatMain chatMain){
//        System.out.println("=========postPersist=======");
//        ChatMain findChatMain = chatMainRepository.getById(chatMain.getId());
//        chatMainRepository.updateForImgPath("asd", findChatMain.getId());
//    }
//}
