package com.makurohashami.realtorconnect.dto.realestate.photo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealEstatePhotoUpdateDto {

    private boolean isPrivate;

}
