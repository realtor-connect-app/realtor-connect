package com.makurohashami.realtorconnect.service.file;

import com.makurohashami.realtorconnect.entity.realestate.RealEstate;
import com.makurohashami.realtorconnect.entity.user.User;
import java.util.Map;

public interface FileParamsGenerator {

    Map<String, Object> generateParamsForAvatar(User user);

    Map<String, Object> generateParamsForRealEstatePhoto(RealEstate realEstate);

}
