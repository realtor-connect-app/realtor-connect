package com.makurohashami.realtorconnect.cdn.service;

import com.makurohashami.realtorconnect.cdn.model.FileUploadRequest;
import com.makurohashami.realtorconnect.cdn.model.FileUploadResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(FileService.class)
public class PlaceholderFileService implements FileService {

    @Value("${network.defaultAvatarUrl}")
    private String defaultAvatarUrl;

    @Override
    public FileUploadResponse uploadFile(FileUploadRequest request) {
        return FileUploadResponse.builder().url(defaultAvatarUrl).build();
    }

    @Override
    public void deleteFile(String path) {
    }

    @Override
    public void deleteFolder(String folder) {
    }

}
