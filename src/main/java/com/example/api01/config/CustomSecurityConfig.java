package com.example.api01.config;


import com.example.api01.security.APIUserDetailsService;
import com.example.api01.security.filter.APILoginFilter;
import com.example.api01.security.handler.APILoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Log4j2
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // 필요한 화면에만 보안설정을 할 수있는 어노테이션
public class CustomSecurityConfig {

    //주입필요
    private final APIUserDetailsService apiUserDetailsService;


    // 로그인 화면에서 로그인을 진행한다. 설정을 통해 사용자가 접근을 제어한다.
    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {


        //csrf란 보안기술이 있는데 이를 처리하기 위한 과정이 복잡하므로 disable처리한다.
        http.csrf().disable();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);


        //API 설정 시작
        // AuthenticationManager 설정
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(
                AuthenticationManagerBuilder.class);

        authenticationManagerBuilder.userDetailsService(apiUserDetailsService)
                .passwordEncoder(passwordEncoder());

        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        http.authenticationManager(authenticationManager);

        APILoginFilter apiLoginFilter = new APILoginFilter("/generateToken");
        apiLoginFilter.setAuthenticationManager(authenticationManager);

        APILoginSuccessHandler successHandler = new APILoginSuccessHandler();
        apiLoginFilter.setAuthenticationSuccessHandler(successHandler);

        //APILoginFilter의 위치 재조정
        http.addFilterBefore(apiLoginFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }


    // js ,css 같이 정적인 파일에는 필터링 하지않도록 조치
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {

        return (web -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()));


    }


    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


}




