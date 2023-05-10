package com.example.api01.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class JWTUtil {

    @Value("${com.example.api01.jwt.secret}")
    private String key;

    public String generateToken(Map<String, Object> valueMap, int days) {


        //헤더부분
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");

        //payload 설정
        Map<String, Object> payloads = new HashMap<>();
        payloads.putAll(valueMap);

        //테스트시에는 짧은 유효기간
        int time = (10) * days;// 테스트는 분단위로 나중에 60*24 (일)단위로 변경

        String jwtStr = Jwts.builder()
                            .setHeader(headers)
                            .setClaims(payloads)
                            .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                            .setExpiration(
                                Date.from(ZonedDateTime.now().plusMinutes(time).toInstant()))// 운영엔 plusday로 설정해 유효기간을 늘려야한다.
                            .signWith(SignatureAlgorithm.HS256, key.getBytes())
                            .compact();

        return jwtStr;

    }

    public Map<String, Object> validToken(String token) throws JwtException {
        Map<String, Object> claim = null;

        claim = Jwts.parser()
                    .setSigningKey(key.getBytes())
                    .parseClaimsJws(token)// 파싱 및 검증 , 실패 시 에러
                    .getBody();

        return claim;
    }


}
