package com.naeggeodo.oauth.config;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class OauthProvider {
    private final String clientId;
    private final String clientSecret;
    private final String loginUri;
    private final String redirectUrl;
    private final String tokenUrl;
    private final String userInfoUrl;

    public OauthProvider(OauthProperties.User user, OauthProperties.Provider provider) {
        this(user.getClientId(), user.getClientSecret(), user.getLoginUri(), user.getRedirectUri(), provider.getTokenUri(), provider.getUserInfoUri());
    }

    @Builder
    public OauthProvider(String clientId, String clientSecret, String loginUri, String redirectUrl, String tokenUrl, String userInfoUrl) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.loginUri = loginUri;
        this.redirectUrl = redirectUrl;
        this.tokenUrl = tokenUrl;
        this.userInfoUrl = userInfoUrl;
    }

}
