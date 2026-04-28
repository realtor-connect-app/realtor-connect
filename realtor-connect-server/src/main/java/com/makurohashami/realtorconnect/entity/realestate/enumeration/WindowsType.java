package com.makurohashami.realtorconnect.entity.realestate.enumeration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Getter;

@Getter
public enum WindowsType {

    METAL_PLASTIC(0),
    WOODEN(1);

    private static final Map<Integer, WindowsType> TYPE_BY_ID_MAP = Collections.unmodifiableMap(initialiseValueMapping());
    private final int typeId;

    WindowsType(int typeId) {
        this.typeId = typeId;
    }

    public static WindowsType getById(int typeId) {
        return Optional.ofNullable(TYPE_BY_ID_MAP.get(typeId))
                .orElseThrow(() -> new IllegalArgumentException("Can't find WindowsType with id: " + typeId));
    }

    private static Map<Integer, WindowsType> initialiseValueMapping() {
        return Stream.of(values())
                .collect(HashMap::new, (map, type) -> map.put(type.typeId, type), HashMap::putAll);
    }

}
