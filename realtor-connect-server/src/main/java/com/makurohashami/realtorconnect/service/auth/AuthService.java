package com.makurohashami.realtorconnect.service.auth;

import com.makurohashami.realtorconnect.dto.auth.AuthRequest;
import com.makurohashami.realtorconnect.dto.auth.AuthResponse;
import com.makurohashami.realtorconnect.entity.user.User;
import com.makurohashami.realtorconnect.mapper.UserMapper;
import com.makurohashami.realtorconnect.service.user.UserService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;

    @Counted(value = "realtorconnect.auth.login")
    @Timed(value = "realtorconnect.auth.login", histogram = true)
    public AuthResponse authenticate(AuthRequest request) {
        Authentication requestToken = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        Authentication authentication = authenticationManager.authenticate(requestToken);
        User user = (User) authentication.getPrincipal();
        userService.updateLastLogin(user.getId());
        String jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(new AuthResponse.Token(jwtToken))
                .user(userMapper.toFullDto(user))
                .build();
    }
}
