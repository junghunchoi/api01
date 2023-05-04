package com.example.api01.security.filter;


import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.log4j.Log4j2;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;


// AbstractAuthenticationProcessingFilter : id 와 pw를 통해 jwt 분자열 반환 클래스
@Log4j2
public class APILoginFilter extends AbstractAuthenticationProcessingFilter {

    public APILoginFilter(String defalutFilterProcessUrl) {
        super(defalutFilterProcessUrl);
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
        HttpServletResponse response)
        throws AuthenticationException, IOException, ServletException {

        log.info("------APILoginFilter------");

        if (request.getMethod().equalsIgnoreCase("GET")) {
            log.info("GET METHOD NOT SUPPORT!");
            return null;
        }

        Map<String, String> jsonData = parseRequestJSON(request);

        log.info("jsonData : " + jsonData);

        // id와 pw로 token 인증정보를 만든 후 필터에서 처리
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            jsonData.get("mid"), jsonData.get("mpw"));

        log.info("authenticationToken : " + authenticationToken);

        return getAuthenticationManager().authenticate(authenticationToken);
    }



    public Map<String, String> parseRequestJSON(HttpServletRequest request) {

        //json 데이터를 분석해서 mid, mpw 전달 값을 map으로 처리
        try (Reader reader = new InputStreamReader(request.getInputStream())) {

            Gson gson = new Gson();

            return gson.fromJson(reader, Map.class);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
