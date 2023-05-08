package com.example.api01.security.exception;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import javax.imageio.IIOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;


public class AccessTokenException extends RuntimeException{

    TOKEN_ERROR token_error;

    public enum TOKEN_ERROR{
        UNACCEPT(401, "Token is null or too short"),
        BADTYPE(401, "token type bearer"),
        MALFORM(401, "Malformed Token"),
        BADSIGN(401, "BadSignatured Token"),
        EXPIRED(401, "Expired Token");

        private int status;
        private String msg;

        TOKEN_ERROR(int status, String msg) {
            this.status = status;
            this.msg = msg;
        }

        public int getStatus() {
            return status;
        }

        public String getMsg() {
            return msg;
        }
    }


    public AccessTokenException(TOKEN_ERROR error) {
        super(error.name());
        this.token_error = error;
    }

    public void sendResponseError(HttpServletResponse response) {

        response.setStatus(token_error.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Gson gson = new Gson();

        String reponseStr = gson.toJson(Map.of("msg", token_error.getMsg(), "time", new Date()));

        try {
            response.getWriter().println("reponseStr : " + reponseStr);
        } catch (IOException E) {
            throw new RuntimeException();
        }
    }
}
