package com.example.api01.security.filter;

import com.example.api01.security.exception.AccessTokenException;
import com.example.api01.security.exception.AccessTokenException.TOKEN_ERROR;
import com.example.api01.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import java.io.IOException;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.filter.OncePerRequestFilter;

@Log4j2
@RequiredArgsConstructor
public class TokenCheckFilter extends OncePerRequestFilter { //OncePerRequestFilter 하나의 요청에 한번의 동작

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (!path.startsWith("/api/")) {
            filterChain.doFilter(request,response);
            return;
        }

        log.info("Token Check Filter ....");
        log.info("JWTUtil : " + jwtUtil);

        // try에서 에러 객체를 생성하고 처리하는거구만
        try {
            validateAccessToken(request);
            filterChain.doFilter(request, response);
        } catch (AccessTokenException accessTokenException) {
            accessTokenException.sendResponseError(response);
        }

    }

    private Map<String, Object> validateAccessToken(HttpServletRequest request) throws AccessTokenException {

        String headStr = request.getHeader("Authorization");

        if (headStr == null || headStr.length() < 8) {
            throw new AccessTokenException(TOKEN_ERROR.UNACCEPT);
        }

        String tokenType = headStr.substring(0, 6);
        String tokenStr = headStr.substring(7);

        if (tokenType.equalsIgnoreCase("Bearer") == false) {
            throw new AccessTokenException(TOKEN_ERROR.BADTYPE);
        }

        try {
            Map<String, Object> values = jwtUtil.validToken(tokenStr);

            return values;
        } catch (MalformedJwtException malformedJwtException) {
            log.error("MalformedJwtException..");
            throw new AccessTokenException(TOKEN_ERROR.MALFORM);
        } catch (SignatureException signatureException) {
            log.error("SignatureException...");
            throw new AccessTokenException(TOKEN_ERROR.BADSIGN);
        } catch (ExpiredJwtException expiredJwtException) {
            log.error("ExpiredJwtException");
            throw new AccessTokenException(TOKEN_ERROR.EXPIRED);
        }
    }
}
