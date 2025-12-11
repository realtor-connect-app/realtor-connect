package com.makurohashami.realtorconnect.service.file.placeholder;

import com.makurohashami.realtorconnect.entity.realestate.RealEstate;
import com.makurohashami.realtorconnect.entity.user.User;
import com.makurohashami.realtorconnect.service.file.FileParamsGenerator;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
@Conditional(PlaceholderFileEnabledCondition.class)
public class PlaceholderFileParamsGenerator implements FileParamsGenerator {

    @Override
    public Map<String, Object> generateParamsForAvatar(User user) {
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> generateParamsForRealEstatePhoto(RealEstate realEstate) {
        return new HashMap<>();
    }

}
