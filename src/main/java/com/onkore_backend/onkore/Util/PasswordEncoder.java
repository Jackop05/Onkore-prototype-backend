package com.onkore_backend.onkore.Util;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoder {

    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder BCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
