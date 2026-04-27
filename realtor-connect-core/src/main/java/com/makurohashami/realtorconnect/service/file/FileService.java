package com.makurohashami.realtorconnect.service.file;

import com.makurohashami.realtorconnect.dto.file.FileUploadResponse;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    FileUploadResponse uploadFile(MultipartFile file, Map<String, Object> params);

    void deleteFile(String path);

    void deleteFolder(String folder);

}
