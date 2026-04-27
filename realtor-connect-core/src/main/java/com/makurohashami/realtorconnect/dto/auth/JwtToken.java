package com.makurohashami.realtorconnect.dto.auth;

import com.makurohashami.realtorconnect.entity.user.Role;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtToken {

    private String username;
    private Role role;
    private Instant expiration;

}
