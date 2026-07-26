package com.orderledger.service;


import com.orderledger.dao.entity.UserEntity;
import com.orderledger.dao.repository.UserRepository;
import com.orderledger.dto.request.LoginRequest;
import com.orderledger.dto.request.RefreshTokenRequest;
import com.orderledger.dto.request.RegisterRequest;
import com.orderledger.dto.response.JwtResponse;
import com.orderledger.exception.UserNotFound;
import com.orderledger.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Bu email artıq istifadə olunub.");
        }
        userRepository.save(userMapper.toEnt(request));
        return "İstifadəçi uğurla qeydiyyatdan keçdi.";
    }

    public JwtResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFound("Email və ya şifrə səhv daxil edilib!"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UserNotFound("Email və ya şifrə səhv daxil edilib!");
        }
        String refreshToken = jwtService.generateRefreshToken(request.getEmail(),user.getRole());
        String accessToken = jwtService.generateAccessToken(request.getEmail(),user.getRole());
        return new JwtResponse(refreshToken, accessToken);
    }

    public JwtResponse refresh(RefreshTokenRequest request) {
        String email = jwtService.extractEmail(request.getRefreshToken());

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı"));

        if (jwtService.isTokenValid(request.getRefreshToken(), user.getEmail())) {
            String newAccessToken = jwtService.generateAccessToken(user.getEmail(),user.getRole());
            return new JwtResponse(newAccessToken, request.getRefreshToken());
        }
        throw new RuntimeException("Refresh token etibarsızdır!");
    }

}
