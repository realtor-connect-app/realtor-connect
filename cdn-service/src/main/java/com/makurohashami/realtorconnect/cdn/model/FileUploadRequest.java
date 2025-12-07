package com.makurohashami.realtorconnect.cdn.model;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadRequest {

    private byte[] bytes;
    private Map<String, Object> params;

}
