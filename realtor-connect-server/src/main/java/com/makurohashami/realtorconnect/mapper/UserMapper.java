package com.makurohashami.realtorconnect.mapper;

import com.makurohashami.realtorconnect.dto.user.UserAddDto;
import com.makurohashami.realtorconnect.dto.user.UserDto;
import com.makurohashami.realtorconnect.dto.user.UserFullDto;
import com.makurohashami.realtorconnect.entity.user.User;
import java.util.List;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Value("${network.defaultAvatarUrl}")
    private String avatarUrl;

    @Named("getDefaultAvatarUrl")
    public String getDefaultAvatarUrl() {
        return avatarUrl;
    }

    @Named("encodePassword")
    public String encodePassword(String rawPassword) {
        return new BCryptPasswordEncoder().encode(rawPassword);
    }

    public abstract UserDto toDto(User user);

    public abstract UserFullDto toFullDto(User user);

    @IterableMapping(elementTargetType = UserFullDto.class)
    public abstract List<UserFullDto> toListFullDto(List<User> users);

    @Mapping(source = "password", target = "password", qualifiedByName = "encodePassword")
    @Mapping(target = "avatar", expression = "java( this.getDefaultAvatarUrl() )")
    @Mapping(target = "blocked", constant = "false")
    @Mapping(target = "emailVerified", constant = "false")
    public abstract User toEntity(UserAddDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "password", target = "password", qualifiedByName = "encodePassword")
    public abstract User update(@MappingTarget User user, UserAddDto dto);
}
