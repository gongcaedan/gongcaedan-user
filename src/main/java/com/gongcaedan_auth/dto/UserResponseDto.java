package com.gongcaedan_auth.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private String id;
    private String email;
    private String username;
    private String nickname;
    private boolean role;
    private LocalDateTime createdAt;
}
