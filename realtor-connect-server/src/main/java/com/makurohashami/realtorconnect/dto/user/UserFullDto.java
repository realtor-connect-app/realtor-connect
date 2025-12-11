package com.makurohashami.realtorconnect.dto.user;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserFullDto extends UserDto {

    private String username;
    private String email;
    private String phone;
    private Instant lastLogin;
    private Boolean blocked;
    private Boolean emailVerified;

}
