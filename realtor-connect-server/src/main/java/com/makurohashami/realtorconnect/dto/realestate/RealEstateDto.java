package com.makurohashami.realtorconnect.dto.realestate;

import com.makurohashami.realtorconnect.dto.realestate.photo.RealEstatePhotoDto;
import com.makurohashami.realtorconnect.entity.realestate.embedded.Area;
import com.makurohashami.realtorconnect.entity.realestate.embedded.Bathroom;
import com.makurohashami.realtorconnect.entity.realestate.embedded.Loggia;
import com.makurohashami.realtorconnect.entity.realestate.enumeration.AnnouncementType;
import com.makurohashami.realtorconnect.entity.realestate.enumeration.BuildingType;
import com.makurohashami.realtorconnect.entity.realestate.enumeration.HeatingType;
import com.makurohashami.realtorconnect.entity.realestate.enumeration.HotWaterType;
import com.makurohashami.realtorconnect.entity.realestate.enumeration.StateType;
import com.makurohashami.realtorconnect.entity.realestate.enumeration.WindowsType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RealEstateDto {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private boolean verified;
    private LocationDto location;
    private Loggia loggia;
    private Bathroom bathroom;
    private Area area;
    private short floor;
    private short floorsInBuilding;
    private BuildingType buildingType;
    private HeatingType heatingType;
    private WindowsType windowsType;
    private HotWaterType hotWaterType;
    private StateType stateType;
    private AnnouncementType announcementType;
    private short roomsCount;
    private double ceilingHeight;
    private Instant calledAt;
    private List<RealEstatePhotoDto> photos = new ArrayList<>();
    private boolean isPrivate;
    private long realtorId;

}
