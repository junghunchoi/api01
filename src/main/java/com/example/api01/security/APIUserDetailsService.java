package com.example.api01.security;

import com.example.api01.Repository.APIUserRepository;
import com.example.api01.dto.APIUserDTO;
import com.example.api01.entity.APIUser;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class APIUserDetailsService implements UserDetailsService {

    private final APIUserRepository apiUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<APIUser> result = apiUserRepository.findById(username);

        APIUser apiUser = result.orElseThrow(
            () -> new UsernameNotFoundException("cannot find mid"));

        APIUserDTO dto = new APIUserDTO(apiUser.getMid(), apiUser.getMpw(),
            List.of(new SimpleGrantedAuthority("ROLE_USER")));

        log.info("apiuserdto.auth : " + dto.getAuthorities());

        return dto;
    }
}
