package com.makurohashami.realtorconnect.file;

import com.makurohashami.realtorconnect.BaseISpec;
import com.makurohashami.realtorconnect.dto.file.FileUploadResponse;
import com.makurohashami.realtorconnect.entity.realestate.RealEstate;
import com.makurohashami.realtorconnect.entity.user.User;
import com.makurohashami.realtorconnect.service.file.FileParamsGenerator;
import com.makurohashami.realtorconnect.service.file.FileService;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
public class FileServiceITest extends BaseISpec {

    @Autowired
    FileService fileService;
    @Autowired
    FileParamsGenerator fileParamsGenerator;

    @Test
    public void generateParamsForAvatarTest() {
        //when
        Map<String, Object> params = fileParamsGenerator.generateParamsForAvatar(new User());

        //then
        assertThat(params, notNullValue());
    }

    @Test
    public void generateParamsForRealEstatePhoto() {
        //when
        Map<String, Object> params = fileParamsGenerator.generateParamsForRealEstatePhoto(new RealEstate());

        //then
        assertThat(params, notNullValue());
    }

    @Test
    public void uploadFileTest() {
        //when
        FileUploadResponse response = fileService.uploadFile(null, new HashMap<>());

        //then
        assertThat(response, notNullValue());
    }

    @Test
    public void deleteFileWithoutExceptionTest() {
        //when
        fileService.deleteFile("filename.jpg");

    }

    @Test
    public void deleteFolderWithoutExceptionTest() {
        //when
        fileService.deleteFolder("/folder");
    }

}
