package com.gongcaedan_auth.service;

import com.gongcaedan_auth.dto.TokenResponseDto;
import com.gongcaedan_auth.dto.UserRequestDto;
import com.gongcaedan_auth.dto.UserResponseDto;
import com.gongcaedan_auth.entity.User;
import com.gongcaedan_auth.repository.UserRepository;
import com.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private final BCryptPasswordEncoder passwordEncoder;

    // 회원가입
    public UserResponseDto register(UserRequestDto dto) {
        if (userRepository.findAll().stream().anyMatch(u -> u.getEmail().equals(dto.getEmail()))) {
            throw new RuntimeException("이미 가입된 이메일입니다.");
        }

        User user = User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .username(dto.getUsername())
                .nickname(dto.getNickname())
                .role(false)
                .build();

        User saved = userRepository.save(user);
        return UserResponseDto.builder()
                .id(saved.getId())
                .email(saved.getEmail())
                .username(saved.getUsername())
                .nickname(saved.getNickname())
                .role(saved.isRole())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    // 로그인
    public TokenResponseDto login(String email, String password) {
        Optional<User> userOpt = userRepository.findAll().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst();

        if (userOpt.isEmpty() || !passwordEncoder.matches(password, userOpt.get().getPassword())) {
            throw new RuntimeException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        User user = userOpt.get();

        // 토큰 생성
        String accessToken = jwtUtil.createToken(user.getEmail(), user.isRole() ? "admin" : "user", "access");
        String refreshToken = jwtUtil.createToken(user.getEmail(), user.isRole() ? "admin" : "user", "refresh");

        // Redis에 Refresh Token 저장 (7일 유효)
        redisTemplate.opsForValue().set("RT:" + user.getEmail(), refreshToken, 7, TimeUnit.DAYS);

        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 로그아웃
    public void logout(String email) {
        redisTemplate.delete("RT:" + email);
    }

    // Refresh Token 재발급
    public String reissue(String refreshToken) {
        String email = jwtUtil.getEmail(refreshToken);
        String storedToken = (String) redisTemplate.opsForValue().get("RT:" + email);

        if (storedToken == null || !storedToken.equals(refreshToken) || jwtUtil.isExpired(refreshToken)) {
            throw new RuntimeException("유효하지 않은 리프레시 토큰입니다.");
        }

        return jwtUtil.createToken(email, jwtUtil.getRole(refreshToken), "access");
    }
}