package com.makurohashami.realtorconnect.dto.realestate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {

    private String city;
    private String district;
    private String residentialArea;
    private String street;
    private String housingEstate;
    private String landmark;

}
