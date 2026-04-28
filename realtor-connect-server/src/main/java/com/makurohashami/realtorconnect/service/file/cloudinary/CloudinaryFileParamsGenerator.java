package com.makurohashami.realtorconnect.service.file.cloudinary;

import com.cloudinary.EagerTransformation;
import com.makurohashami.realtorconnect.config.CloudinaryConfiguration.CloudinaryEnabled;
import com.makurohashami.realtorconnect.config.FileConfiguration;
import com.makurohashami.realtorconnect.entity.realestate.RealEstate;
import com.makurohashami.realtorconnect.entity.user.User;
import com.makurohashami.realtorconnect.service.file.FileParamsGenerator;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Conditional(CloudinaryEnabled.class)
public class CloudinaryFileParamsGenerator implements FileParamsGenerator {

    private final FileConfiguration fileConfiguration;
    @Value("${network.defaultAvatarUrl}")
    private String defaultAvatarUrl;
    @Value("${cloudinary.appPrefix}")
    private String cloudinaryAppPrefix;

    private String getAppPrefix() {
        return cloudinaryAppPrefix.isBlank() ? "" : cloudinaryAppPrefix.endsWith("/") ? cloudinaryAppPrefix : cloudinaryAppPrefix + "/";
    }

    @Override
    public Map<String, Object> generateParamsForAvatar(User user) {

        int sizeForSave = fileConfiguration.getAvatar().getWidthHeightForSave();
        EagerTransformation transformation = new EagerTransformation().height(sizeForSave).width(sizeForSave).crop("fill").gravity("auto");

        Map<String, Object> params = new HashMap<>();
        params.put("tags", "avatar");
        params.put("transformation", transformation);
        if (user.getAvatar() != null && !user.getAvatar().equals(defaultAvatarUrl)) {
            params.put("public_id", user.getAvatarId());
        } else {
            params.put("folder", getAppPrefix() + "avatars");
        }

        return params;
    }

    @Override
    public Map<String, Object> generateParamsForRealEstatePhoto(RealEstate realEstate) {
        Map<String, Object> params = new HashMap<>();
        params.put("tags", "realEstatePhoto");
        params.put("folder", getAppPrefix() + "realestates/" + realEstate.getId());
        return params;
    }
}
