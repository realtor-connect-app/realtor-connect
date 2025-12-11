package com.makurohashami.realtorconnect.auth;

import com.makurohashami.realtorconnect.BaseISpec;
import com.makurohashami.realtorconnect.dto.auth.AuthRequest;
import com.makurohashami.realtorconnect.dto.auth.AuthResponse;
import com.makurohashami.realtorconnect.entity.user.Role;
import com.makurohashami.realtorconnect.entity.user.User;
import com.makurohashami.realtorconnect.repository.UserRepository;
import com.makurohashami.realtorconnect.service.auth.AuthService;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@WithAnonymousUser
public class AuthServiceITest extends BaseISpec {

    @Autowired
    AuthService authService;

    @Autowired
    UserRepository userRepository;

    String pass = "pass";

    User user = User.builder()
            .name("user")
            .email("user@mail.com")
            .username("user")
            .password(new BCryptPasswordEncoder().encode(pass))
            .phone("+380000000000")
            .role(Role.REALTOR)
            .blocked(false)
            .emailVerified(true)
            .build();

    @BeforeEach
    public void init(@Autowired Flyway flyway) {
        flyway.clean();
        flyway.migrate();

        userRepository.save(user);
    }

    @Test
    public void authenticateTest() {
        //given
        AuthRequest request = AuthRequest.builder()
                .username(user.getUsername())
                .password(pass)
                .build();

        //when
        AuthResponse response = authService.authenticate(request);

        //then
        assertThat(response, notNullValue());
        assertThat(response.getToken().getAuthToken(), notNullValue());
        assertThat(response.getUser().getId(), is(user.getId()));
    }

    @Test
    public void badAuthenticateTest() {
        //given
        AuthRequest request = AuthRequest.builder()
                .username(user.getUsername())
                .password("bad_" + pass)
                .build();

        //when
        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> authService.authenticate(request));

        //then
        assertThat(exception, notNullValue());
        assertThat(exception.getMessage(), notNullValue());
    }

}
