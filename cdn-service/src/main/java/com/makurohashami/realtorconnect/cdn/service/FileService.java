package com.makurohashami.realtorconnect.cdn.service;

import com.makurohashami.realtorconnect.cdn.model.FileUploadRequest;
import com.makurohashami.realtorconnect.cdn.model.FileUploadResponse;

public interface FileService {

    FileUploadResponse uploadFile(FileUploadRequest request);

    void deleteFile(String path);

    void deleteFolder(String folder);

}
