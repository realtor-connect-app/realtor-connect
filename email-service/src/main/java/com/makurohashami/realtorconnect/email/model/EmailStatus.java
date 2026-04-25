package com.makurohashami.realtorconnect.email.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailStatus {

    NEW(0),
    PROCESSING(1),
    SENT(2),
    FAILED(3);

    private final int id;

    private static final Map<Integer, EmailStatus> STATUS_BY_ID_MAP = Collections.unmodifiableMap(initialiseValueMapping());

    private static Map<Integer, EmailStatus> initialiseValueMapping() {
        return Stream.of(values()).collect(HashMap::new,
                (map, status) -> map.put(status.id, status), HashMap::putAll
        );
    }

}
