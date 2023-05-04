package com.example.api01.dto;


import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Getter
@Setter
@ToString
public class APIUserDTO extends User {

    private String mid;
    private String mpw;

    public APIUserDTO(String username, String password, Collection<GrantedAuthority> authorities) {
        super(username,password,authorities);
        this.mid = mid;
        this.mpw = mpw;
    }
}
