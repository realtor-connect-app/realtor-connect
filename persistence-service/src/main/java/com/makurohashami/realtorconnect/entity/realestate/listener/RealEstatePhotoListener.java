package com.makurohashami.realtorconnect.entity.realestate.listener;

import com.makurohashami.realtorconnect.cdn.service.FileService;
import com.makurohashami.realtorconnect.entity.realestate.RealEstatePhoto;
import jakarta.persistence.PostRemove;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RealEstatePhotoListener {

    private static FileService fileService;

    @Autowired
    public void init(FileService fileService) {
        RealEstatePhotoListener.fileService = fileService;
    }

    @PostRemove
    public void postRemove(RealEstatePhoto photo) {
        if (photo.getPhotoId() != null) {
            fileService.deleteFile(photo.getPhotoId());
        }
    }

}
