package com.makurohashami.realtorconnect.entity.realestate.enumeration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Getter;

@Getter
public enum AnnouncementType {

    SALE(0),
    DAILY_RENT(1),
    LONG_RENT(2);

    private static final Map<Integer, AnnouncementType> TYPE_BY_ID_MAP = Collections.unmodifiableMap(initialiseValueMapping());
    private final int typeId;

    AnnouncementType(int typeId) {
        this.typeId = typeId;
    }

    public static AnnouncementType getById(int typeId) {
        return Optional.ofNullable(TYPE_BY_ID_MAP.get(typeId))
                .orElseThrow(() -> new IllegalArgumentException("Can't find AnnouncementType with id: " + typeId));
    }

    private static Map<Integer, AnnouncementType> initialiseValueMapping() {
        return Stream.of(values())
                .collect(HashMap::new, (map, type) -> map.put(type.typeId, type), HashMap::putAll);
    }

}
