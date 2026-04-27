package com.makurohashami.realtorconnect.dto.auth;

import com.makurohashami.realtorconnect.dto.user.UserFullDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    private Token token;
    private UserFullDto user;

    @Getter
    @AllArgsConstructor
    public static class Token {
        private String authToken;
    }

}
