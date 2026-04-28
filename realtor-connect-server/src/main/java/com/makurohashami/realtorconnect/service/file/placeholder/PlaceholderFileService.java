package com.makurohashami.realtorconnect.service.file.placeholder;

import com.makurohashami.realtorconnect.dto.file.FileUploadResponse;
import com.makurohashami.realtorconnect.service.file.FileService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Primary
@Component
@Conditional(PlaceholderFileEnabledCondition.class)
public class PlaceholderFileService implements FileService {

    @Value("${network.defaultAvatarUrl}")
    private String defaultAvatarUrl;

    @Override
    @Counted(value = "realtorconnect.file.service")
    @Timed(value = "realtorconnect.file.service", histogram = true)
    public FileUploadResponse uploadFile(MultipartFile file, Map<String, Object> params) {
        return FileUploadResponse.builder().url(defaultAvatarUrl).build();
    }

    @Override
    @Counted(value = "realtorconnect.file.service")
    @Timed(value = "realtorconnect.file.service", histogram = true)
    public void deleteFile(String path) {
    }

    @Override
    @Counted(value = "realtorconnect.file.service")
    @Timed(value = "realtorconnect.file.service", histogram = true)
    public void deleteFolder(String folder) {
    }

}
