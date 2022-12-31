package com.naeggeodo.oauth.config;

import java.util.HashMap;
import java.util.Map;

//properties를 플랫폼별로 가지고 있다 
//client에서 넘어오는 provider값으로 해당하는 플랫폼의 데이터만 넘겨줌
public class InMemoryProviderRepository {
    private final Map<String, OauthProvider> providers;

    public InMemoryProviderRepository(Map<String, OauthProvider> providers) {
        this.providers = new HashMap<>(providers);
    }

    public OauthProvider findByProviderName(String name) {
        return providers.get(name);
    }
}
