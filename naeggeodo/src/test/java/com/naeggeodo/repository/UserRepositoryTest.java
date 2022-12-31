package com.naeggeodo.repository;


import com.naeggeodo.dto.MypageDTO;
import com.naeggeodo.entity.user.Authority;
import com.naeggeodo.entity.user.Users;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@Sql("classpath:h2/userRepository.sql")
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("퀵챗 패치 조인")
    void findQuickChatEntityGraphTest() {
        Optional<Users> user0 = userRepository.findQuickChatEntityGraph("user0");
        Optional<Users> user = userRepository.findQuickChatEntityGraph("user");

        assertAll(
                () -> assertThat(user0).isPresent(),
                () -> assertThat(user0).map(i -> i.getQuickChat().getMsg1()).hasValue("반갑습니다 *^ㅡ^*"),
                () -> assertThat(user0).map(i -> i.getQuickChat().getMsg2()).hasValue("주문 완료했습니다! 송금 부탁드려요 *^ㅡ^*"),
                () -> assertThat(user0).map(i -> i.getQuickChat().getMsg3()).hasValue("음식이 도착했어요!"),
                () -> assertThat(user0).map(i -> i.getQuickChat().getMsg4()).hasValue("맛있게 드세요 *^ㅡ^*"),
                () -> assertThat(user0).map(i -> i.getQuickChat().getMsg5()).hasValue("주문내역 확인해주세요!"),
                () -> assertThat(user).isEmpty()
        );
    }

    @Test
    @DisplayName("마이페이지")
    void getMyPageDataTest1() {
        Optional<MypageDTO> dto = userRepository.getMyPageData("user0");
        assertAll(
                () -> assertThat(dto.map(MypageDTO::getParticipatingChatCount)).hasValue(1L),
                () -> assertThat(dto.map(MypageDTO::getMyOrdersCount)).hasValue(1L),
                () -> assertThat(dto.map(MypageDTO::getNickname)).hasValue("도봉산-왕주먹")
        );
    }

    @Test
    @DisplayName("마이페이지 - 카운트 데이터가 없을때는 0을 리턴한다.")
    void getMyPageDataTest2() {
        Optional<MypageDTO> dto = userRepository.getMyPageData("user1");

        assertAll(
                () -> assertThat(dto.map(MypageDTO::getParticipatingChatCount)).hasValue(0L),
                () -> assertThat(dto.map(MypageDTO::getMyOrdersCount)).hasValue(0L),
                () -> assertThat(dto.map(MypageDTO::getNickname)).hasValue("까치산-왕발바닥")
        );
    }

    @Test
    @DisplayName("소셜 ID , 권한으로 찾기")
    void findBySocialIdAndAuthorityTest() {
        Users user = userRepository.findBySocialIdAndAuthority("1234", Authority.MEMBER);

        assertThat(user.getId()).isEqualTo("user1");
    }
}