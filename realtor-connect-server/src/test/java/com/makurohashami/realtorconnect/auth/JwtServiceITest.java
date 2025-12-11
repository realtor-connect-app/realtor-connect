package com.makurohashami.realtorconnect.auth;

import com.makurohashami.realtorconnect.BaseISpec;
import com.makurohashami.realtorconnect.dto.auth.JwtToken;
import com.makurohashami.realtorconnect.entity.user.Role;
import com.makurohashami.realtorconnect.entity.user.User;
import com.makurohashami.realtorconnect.repository.UserRepository;
import com.makurohashami.realtorconnect.service.auth.JwtService;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@WithAnonymousUser
public class JwtServiceITest extends BaseISpec {

    @Autowired
    JwtService jwtService;
    @Autowired
    UserRepository userRepository;

    User user = User.builder()
            .name("user")
            .email("user@mail.com")
            .username("user")
            .password(new BCryptPasswordEncoder().encode("pass"))
            .phone("+380000000000")
            .role(Role.REALTOR)
            .blocked(false)
            .emailVerified(true)
            .build();

    @Test
    public void generateTokenTest() {
        //when
        String token = jwtService.generateToken(user);

        //then
        assertThat(token, notNullValue());
    }

    @Test
    public void parseTokenTest() {
        //when
        JwtToken token = jwtService.parseToken(jwtService.generateToken(user));

        //when
        assertThat(token, notNullValue());
        assertThat(token.getExpiration().isAfter(Instant.now()), is(true));
        assertThat(token.getRole(), is(user.getRole()));
        assertThat(token.getUsername(), is(user.getUsername()));
    }

    @Test
    public void parseTokenWithInvalidTokenTest() {
        //when
        Exception asserted = assertThrows(Exception.class, () -> jwtService.parseToken(""));

        //then
        assertThat(asserted, notNullValue());
        assertThat(asserted.getMessage(), notNullValue());
    }

    @Test
    public void isTokenValidTest() {
        //when
        Boolean isValid = jwtService.isTokenValid(jwtService.parseToken(jwtService.generateToken(user)));

        //then
        assertThat(isValid, notNullValue());
        assertThat(isValid, is(true));
    }

}
