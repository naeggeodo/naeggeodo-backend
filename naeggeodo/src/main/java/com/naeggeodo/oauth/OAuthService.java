package com.naeggeodo.oauth;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naeggeodo.entity.chat.QuickChat;
import com.naeggeodo.entity.user.Authority;
import com.naeggeodo.entity.user.Users;
import com.naeggeodo.oauth.config.InMemoryProviderRepository;
import com.naeggeodo.oauth.config.OauthProvider;
import com.naeggeodo.oauth.dto.*;
import com.naeggeodo.repository.QuickChatRepository;
import com.naeggeodo.repository.UserRepository;
import com.naeggeodo.util.RandomNickNameGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class OAuthService {

    @Autowired
    private InMemoryProviderRepository inMemoryProviderRepository;
    @Autowired
    private SocialLogin oauthDetail;
    private UserRepository userRepository;
    private OAuthDtoMapper oauthMapper;

    @Autowired
    private QuickChatRepository quickChatRepository;

    @Transactional
    public SimpleUser getAuth(String code, String providerName) throws Exception {
        Map<String, String> requestHeaders = new HashMap<>();

        log.info("getAuth : ");
        OAuthDto oauthUserInfo = setOAuthDto(providerName);
        OauthProvider provider = inMemoryProviderRepository.findByProviderName(providerName);//application.yml�뿉 �벑濡앸맂 oauth2 �젙蹂�

        OauthAuthorized authorization = oauthDetail.requestAccessToken(code, provider);

        requestHeaders.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        requestHeaders.put("Authorization", "Bearer " + authorization.getAccessToken()); //�쟾遺��떎 String�삎�씪 �븣. RestTemplate �븣臾몄뿉 �깮�왂媛��뒫

        String responseBody = oauthDetail.get(provider.getUserInfoUrl(), requestHeaders);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);//�빐�떦 �븘�뱶媛� �뾾�쓣寃쎌슦 臾댁떆
        new Users().setJoindate(LocalDateTime.now());
        try {
            oauthUserInfo = objectMapper.readValue(
                    responseBody,
                    oauthUserInfo.getClass());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return getUser(oauthUserInfo);

    }

    public SimpleUser getAuth(OauthAuthorized oauthToken, String providerName) {
        log.info("Mobile getAuth : " + oauthToken.toString());
        OAuthDto oauthUserInfo = setOAuthDto(providerName);

        return getUser(
                oauthDetail.requestUserInfo(
                        inMemoryProviderRepository.findByProviderName(providerName).getUserInfoUrl(),
                        oauthToken,
                        oauthUserInfo
                )
        );
    }

    //prider媛믪뿉�뵲�씪 �빐�떦 dto 諛섑솚
    private OAuthDto setOAuthDto(String providerName) {
        switch (providerName) {
            case "naver":
                return new NaverOAuthDto();
            case "kakao":
                return new KakaoOAuthDto();
            default:
                throw new NullPointerException();
        }
    }

    @Transactional
    @NotFound(action = NotFoundAction.IGNORE)
    protected SimpleUser getUser(OAuthDto oauthDto) {
        Users user = userRepository.findBySocialIdAndAuthority(oauthDto.getId(), Authority.MEMBER);

        if (user == null) {
            user = oauthMapper.mappingUser(oauthDto);
            user.setId(UUID.randomUUID().toString());
            user.setAuthority(Authority.MEMBER);
            user.setJoindate(LocalDateTime.now());
            user.setNickname(RandomNickNameGenerator.createRandomNickName());

            QuickChat qc = QuickChat.create(user);
            user.setQuickChat(qc);
            quickChatRepository.save(qc);
        }


        return new SimpleUser(user.getId(), user.getAddress(), user.getAuthority(), user.getBuildingCode());

    }


}

