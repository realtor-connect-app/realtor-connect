package com.makurohashami.realtorconnect.controller;

import com.makurohashami.realtorconnect.config.FileConfiguration;
import com.makurohashami.realtorconnect.config.RealEstateConfiguration;
import com.makurohashami.realtorconnect.config.RealtorConfiguration;
import com.makurohashami.realtorconnect.config.UserConfiguration;
import com.makurohashami.realtorconnect.dto.apiresponse.ApiSuccess;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.makurohashami.realtorconnect.util.ApiResponseUtil.ok;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/settings", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Settings Controller", description = "Get info about current app settings")
public class SettingsController {

    public final FileConfiguration fileConfiguration;
    public final UserConfiguration userConfiguration;
    public final RealtorConfiguration realtorConfiguration;
    public final RealEstateConfiguration realEstateConfiguration;

    @GetMapping("/file")
    @Operation(summary = "Get app file settings")
    public ResponseEntity<ApiSuccess<FileConfigurationDto>> getFileConfiguration() {
        return ok(toFileConfigurationDto(fileConfiguration));
    }

    @GetMapping("/user")
    @Operation(summary = "Get app user settings")
    public ResponseEntity<ApiSuccess<UserConfiguration>> getUserConfiguration() {
        return ok(userConfiguration);
    }

    @GetMapping("/realtor")
    @Operation(summary = "Get app realtor settings")
    public ResponseEntity<ApiSuccess<RealtorConfiguration>> getRealtorConfiguration() {
        return ok(realtorConfiguration);
    }

    @GetMapping("/real-estate")
    @Operation(summary = "Get app real estate settings")
    public ResponseEntity<ApiSuccess<RealEstateConfiguration>> getRealEstateConfiguration() {
        return ok(realEstateConfiguration);
    }

    FileConfigurationDto toFileConfigurationDto(FileConfiguration configuration) {
        return FileConfigurationDto.builder()
                .client(configuration.getClient())
                .allowedContentTypes(configuration.getAllowedContentTypes())
                .avatar(configuration.getAvatar())
                .realEstatePhoto(configuration.getRealEstatePhoto())
                .multipart(configuration.getMultipart())
                .build();
    }

    @Getter
    @Setter
    @Builder
    public static class FileConfigurationDto {

        private String client;
        private List<String> allowedContentTypes;
        private FileConfiguration.AvatarConfig avatar;
        private FileConfiguration.RealEstatePhotoConfig realEstatePhoto;
        private FileConfiguration.MultipartConfig multipart;

        @Getter
        @Setter
        public static class AvatarConfig {
            private int minWidthHeight;
            private int maxWidthHeight;
            private int widthHeightForSave;
        }

        @Getter
        @Setter
        public static class RealEstatePhotoConfig {
            private int minWidthHeight;
            private int maxWidthHeight;
        }

        @Getter
        @Setter
        public static class MultipartConfig {
            private String maxFileSize;
            private String maxRequestSize;
        }
    }

}
