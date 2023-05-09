package com.example.api01.security.filter;


import com.example.api01.security.exception.RefreshTokenException;
import com.example.api01.security.exception.RefreshTokenException.ErrorCase;
import com.example.api01.util.JWTUtil;
import com.google.gson.Gson;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

/*
사용자가 만료된 토큰이 전송되었을 경우에 다시 서버에 토큰을 갱신해달라고 요구해야함
-> accessToken과 Refresh Token의 만료 기간에 따라 토큰을 달리 발급받아야한다.
*/
@Log4j2
@RequiredArgsConstructor
public class RefreshTokenFilter extends OncePerRequestFilter {

    private final String refreshPath;
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (!path.equals(refreshPath)) {
            log.info("skip refresh token filter...");
            filterChain.doFilter(request, response);
            return;
        }

        log.info("refresh token filter .. run ...... 1");

        //전송된 JSON에서 accessToken과 refreshToken을 얻어온다.
        Map<String, String> tokens = parseRequestJSON(request);

        String accessToken = tokens.get("accessToken");
        String refreshToken = tokens.get("refreshToken");

        log.info(accessToken);
        log.info(refreshToken);

        try {
            checkAccessToken(accessToken);
        } catch (RefreshTokenException refreshTokenException) {
            refreshTokenException.sendResponseError(response);
            return;
        }

        Map<String, Object> refreshClaim = null;

        try {
            refreshClaim = checkRefreshToken(refreshToken);
            log.info(refreshToken);

            // Refresh Token의 유효기간이 얼마 남지 않은 경우
            Integer exp = (Integer) refreshClaim.get("exp");

            Date expTime = new Date(Instant.ofEpochMilli(exp).toEpochMilli() * 1000);

            Date currentTime = new Date(System.currentTimeMillis());

            // 만료 시간과 현재 시간의 간격계산
            // 만일 3일 미만인 경우에는 Refresh Token도 다시 생성
            long gapTime = (expTime.getTime() - currentTime.getTime());

            log.info("----------------------");
            log.info("current : " + currentTime);
            log.info("expTime : " + expTime);
            log.info("gap : " + gapTime);

            String mid = (String) refreshClaim.get("mid");

            //이 이상태까지 오면 무조건 AccessToken은 새로 생성
            String accessTokenValue = jwtUtil.generateToken(Map.of("mid", mid), 1);

            String refreshTokenValue = tokens.get("refreshToken");

            //Refresh Token이 3일도 안남았다면
            if (gapTime < (1000 * 60 * 60 * 24 * 3)) {
                log.info("new Refresh Token Required");
                refreshTokenValue = jwtUtil.generateToken(Map.of("mid", mid), 30);
            }

            log.info("Refresh Token Result ....");
            log.info("accessToken : " + accessTokenValue);
            log.info("refreshToken : " + refreshTokenValue);

            sendTokens(accessTokenValue,refreshTokenValue,response);

        } catch (RefreshTokenException refreshTokenException) {
            refreshTokenException.sendResponseError(response);
            return;
        }
    }

    public Map<String, String> parseRequestJSON(HttpServletRequest request) {

        try (Reader reader = new InputStreamReader(request.getInputStream())) {

            Gson gson = new Gson();

            return gson.fromJson(reader, Map.class);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
            return null;
    }

    private void checkAccessToken(String accessToken) throws RefreshTokenException {
        try {
            jwtUtil.validToken(accessToken);
        } catch (ExpiredJwtException expiredJwtException) {
            log.error("Access Token has expired");
        } catch (Exception e) {
            throw new RefreshTokenException(ErrorCase.NO_ACCESS);
        }
    }

    private Map<String, Object> checkRefreshToken(String refreshToken) throws RefreshTokenException {
        try {
            Map<String, Object> values = jwtUtil.validToken(refreshToken);
            return values;
        } catch (ExpiredJwtException expiredJwtException) {
            throw new RefreshTokenException(ErrorCase.OLD_REFRESH);
        } catch (MalformedJwtException malformedJwtException) {
            log.error("MalformedException --------");
            throw new RefreshTokenException(ErrorCase.NO_REFRESH);
        } catch (Exception e) {
            throw new RefreshTokenException(ErrorCase.NO_REFRESH);
        }

    }

    private void sendTokens(String accessTokenValue, String refreshTokenValue,
        HttpServletResponse response) {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Gson gson = new Gson();

        String jsonStr = gson.toJson(Map.of("accessToken", accessTokenValue, "refreshToken",
            refreshTokenValue));

        try {
            response.getWriter().println(jsonStr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
