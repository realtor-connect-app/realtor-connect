package com.makurohashami.realtorconnect.cdn.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.exceptions.NotFound;
import com.cloudinary.utils.ObjectUtils;
import com.makurohashami.realtorconnect.cdn.condition.CloudinaryCdnEnabled;
import com.makurohashami.realtorconnect.cdn.model.FileUploadRequest;
import com.makurohashami.realtorconnect.cdn.model.FileUploadResponse;
import java.io.IOException;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Conditional(CloudinaryCdnEnabled.class)
public class CloudinaryFileService implements FileService {

    private final Cloudinary cloudinary;

    @Override
    public FileUploadResponse uploadFile(FileUploadRequest request) {
        try {
            var result = cloudinary.uploader().upload(request.getBytes(), request.getParams());
            return FileUploadResponse.builder()
                    .url((String) result.get("url"))
                    .fileId((String) result.get("public_id"))
                    .build();
        } catch (IOException ex) {
            log.error("Error while uploading file", ex);
            return FileUploadResponse.builder().build();
        }
    }

    @Async
    @Override
    public void deleteFile(String path) {
        try {
            var params = ObjectUtils.asMap("async", "true");
            cloudinary.api().deleteResources(Collections.singletonList(path), params);
        } catch (Exception ex) {
            log.error("Error while deleting file", ex);
        }
    }

    @Async
    @Override
    public void deleteFolder(String folder) {
        try {
            cloudinary.api().deleteResourcesByPrefix(folder, ObjectUtils.emptyMap());
            cloudinary.api().deleteFolder(folder, ObjectUtils.emptyMap());
        } catch (NotFound ignored) {
        } catch (Exception ex) {
            log.error("Error while deleting folder", ex);
        }
    }

}
