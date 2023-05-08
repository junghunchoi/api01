package com.example.api01.security.exception;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;


public class RefreshTokenException extends RuntimeException{

    private ErrorCase errorCase;

    public enum ErrorCase {
    NO_ACCESS,BAD_ACCESS,NO_REFRESH,OLD_REFRESH,BAD_REFRESH
    }

    public RefreshTokenException(ErrorCase errorCase) {
        super(errorCase.name());

        this.errorCase = errorCase;
    }

    public void sendResponseError(HttpServletResponse response) {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Gson gson = new Gson();

        String responseStr = gson.toJson(Map.of("msg", errorCase.name(), "time", new Date()));

        try {
            response.getWriter().println(responseStr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
