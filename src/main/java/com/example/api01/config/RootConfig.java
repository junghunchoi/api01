package com.example.api01.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RootConfig {

    /*
       ModelMapper
       GETTER, SETTER 없이 자동으로 객체에 필드값을 매칭 시켜주는 라이브러리
     */


    // 아래와 같이 고정으로 사용하고 필요한 설정에 따라 구글링을 통해 설정값을 가져온다.
    @Bean
    public ModelMapper getMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setFieldMatchingEnabled(true)
            .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
            .setMatchingStrategy(MatchingStrategies.LOOSE);
        return modelMapper;
    }
}
