package com.makurohashami.realtorconnect.entity.user;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Getter;

import static com.makurohashami.realtorconnect.entity.user.Permission.ACCESS_TO_ADMIN_PANEL;
import static com.makurohashami.realtorconnect.entity.user.Permission.MANAGE_ADMINS;
import static com.makurohashami.realtorconnect.entity.user.Permission.MANAGE_REALTOR_INFO;
import static com.makurohashami.realtorconnect.entity.user.Permission.MANAGE_USERS;
import static com.makurohashami.realtorconnect.entity.user.Permission.SEE_PRIVATE_PHOTOS;
import static com.makurohashami.realtorconnect.entity.user.Permission.SEE_PRIVATE_REAL_ESTATES;
import static com.makurohashami.realtorconnect.entity.user.Permission.SEE_REALTORS_CONTACTS;

@Getter
public enum Role {

    CHIEF_ADMIN(0, List.of(MANAGE_ADMINS, MANAGE_USERS, MANAGE_REALTOR_INFO, SEE_PRIVATE_PHOTOS, ACCESS_TO_ADMIN_PANEL, SEE_PRIVATE_REAL_ESTATES, SEE_REALTORS_CONTACTS)),
    ADMIN(10, List.of(MANAGE_USERS, MANAGE_REALTOR_INFO, SEE_PRIVATE_PHOTOS, ACCESS_TO_ADMIN_PANEL, SEE_PRIVATE_REAL_ESTATES, SEE_REALTORS_CONTACTS)),
    REALTOR(20, List.of(SEE_PRIVATE_PHOTOS, SEE_REALTORS_CONTACTS)),
    USER(30, List.of(SEE_REALTORS_CONTACTS));

    private static final Map<Integer, Role> ROLE_BY_ID_MAP = Collections.unmodifiableMap(initialiseValueMapping());
    private final int roleId;
    private final List<Permission> permissions;

    Role(int roleId, List<Permission> permissions) {
        this.roleId = roleId;
        this.permissions = permissions;
    }

    public static Role getById(int roleId) {
        return Optional.ofNullable(ROLE_BY_ID_MAP.get(roleId))
                .orElseThrow(() -> new IllegalArgumentException("Can't find Role with id: " + roleId));
    }

    private static Map<Integer, Role> initialiseValueMapping() {
        return Stream.of(values())
                .collect(HashMap::new, (map, role) -> map.put(role.roleId, role), HashMap::putAll);
    }

}
