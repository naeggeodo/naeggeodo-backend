package com.naeggeodo.oauth;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.naeggeodo.oauth.config.OauthConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class TestController {

	@Autowired
	private OauthConfig oauthConfig;
	
	public TestController(OauthConfig oauthConfig) {
		this.oauthConfig = oauthConfig;
	}
	
    @GetMapping(value = "/nnd")
    public String loginPage(){
    	log.info("===========");
    	return "login";
    }
    
    @GetMapping(value = "login/kakao/callAPI")
    public String callAPI(){ 
        return "redirect:https://kauth.kakao.com/oauth/authorize?"
        		+ "response_type=code&client_id=97fb8027ff91de67e7c7bad120325b18"
        		+ "&redirect_uri=http://localhost:8080/oauth/getInfo/kakao";
    }
    @GetMapping(value = "login/test/callAPI")
    public String testAPI(){ 
    	return "redirect:https://kauth.kakao.com/oauth/authorize?"
    			+ "response_type=code&client_id=97fb8027ff91de67e7c7bad120325b18"
    			+ "&redirect_uri=http://localhost:8080/login/getToken/kakao";
    }

    @GetMapping(value = "login/naver/callAPI")
    public String naverCallAPI(){
    	return "redirect:https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=FSZp_zbcpsL0vEkaSrBy&redirect_uri=http://localhost:8080/login/oauth/naver&state=hLiDdL2uhPtsftcU";
    			
    }
    
    @RequestMapping(value = "/responseTest")
    private String fromGoodAccessTokenRequest() {
    	log.info("===========");
    	  return "oauth/getInfo/kakao";
    	}

}
