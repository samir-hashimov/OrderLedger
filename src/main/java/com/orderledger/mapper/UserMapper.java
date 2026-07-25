package com.orderledger.mapper;


import com.orderledger.dao.entity.UserEntity;
import com.orderledger.dto.request.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor

public class UserMapper {
    private final PasswordEncoder passwordEncoder;

    public UserEntity toEnt(RegisterRequest request) {
        return UserEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
    }
}
