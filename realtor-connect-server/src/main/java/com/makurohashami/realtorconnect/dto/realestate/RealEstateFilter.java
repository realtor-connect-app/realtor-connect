package com.makurohashami.realtorconnect.dto.realestate;

import com.makurohashami.realtorconnect.entity.realestate.enumeration.AnnouncementType;
import com.makurohashami.realtorconnect.entity.realestate.enumeration.BathroomType;
import com.makurohashami.realtorconnect.entity.realestate.enumeration.BuildingType;
import com.makurohashami.realtorconnect.entity.realestate.enumeration.HeatingType;
import com.makurohashami.realtorconnect.entity.realestate.enumeration.HotWaterType;
import com.makurohashami.realtorconnect.entity.realestate.enumeration.LoggiaType;
import com.makurohashami.realtorconnect.entity.realestate.enumeration.StateType;
import com.makurohashami.realtorconnect.entity.realestate.enumeration.WindowsType;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealEstateFilter {

    private Long realtorId;
    private String name;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String city;
    private String district;
    private String residentialArea;
    private String street;
    private LoggiaType loggiaType;
    private Short loggiasCount;
    private Boolean isLoggiaGlassed;
    private BathroomType bathroomType;
    private Short bathroomsCount;
    private Boolean isBathroomCombined;
    private Double minTotalArea;
    private Double maxTotalArea;
    private Double minLivingArea;
    private Double maxLivingArea;
    private Double minKitchenArea;
    private Double maxKitchenArea;
    private Short minFloor;
    private Short maxFloor;
    private BuildingType buildingType;
    private HeatingType heatingType;
    private WindowsType windowsType;
    private HotWaterType hotWaterType;
    private StateType stateType;
    private AnnouncementType announcementType;
    private Short roomsCount;

}
