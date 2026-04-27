package com.makurohashami.realtorconnect.dto.realestate;

import com.makurohashami.realtorconnect.entity.realestate.embedded.Area;
import com.makurohashami.realtorconnect.entity.realestate.embedded.Bathroom;
import com.makurohashami.realtorconnect.entity.realestate.embedded.Location;
import com.makurohashami.realtorconnect.entity.realestate.embedded.Loggia;
import com.makurohashami.realtorconnect.entity.realestate.embedded.Owner;
import com.makurohashami.realtorconnect.entity.realestate.enumeration.AnnouncementType;
import com.makurohashami.realtorconnect.entity.realestate.enumeration.BuildingType;
import com.makurohashami.realtorconnect.entity.realestate.enumeration.HeatingType;
import com.makurohashami.realtorconnect.entity.realestate.enumeration.HotWaterType;
import com.makurohashami.realtorconnect.entity.realestate.enumeration.StateType;
import com.makurohashami.realtorconnect.entity.realestate.enumeration.WindowsType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealEstateAddDto {

    @NotNull
    @Size(min = 3, max = 255)
    private String name;
    @NotNull
    @Size(min = 40, max = 512)
    private String description;
    @NotNull
    @DecimalMin("0.01")
    @DecimalMax("999999999999999999.99")
    private BigDecimal price;
    @Valid
    private Owner owner;
    @Valid
    private Location location;
    @Valid
    private Loggia loggia;
    @Valid
    private Bathroom bathroom;
    @Valid
    private Area area;
    @NotNull
    @Min(1)
    @Max(127)
    private short floor;
    @NotNull
    @Min(1)
    @Max(127)
    private short floorsInBuilding;
    @NotNull
    private BuildingType buildingType;
    @NotNull
    private HeatingType heatingType;
    @NotNull
    private WindowsType windowsType;
    @NotNull
    private HotWaterType hotWaterType;
    @NotNull
    private StateType stateType;
    @NotNull
    private AnnouncementType announcementType;
    @NotNull
    @Min(1)
    @Max(127)
    private short roomsCount;
    @Min(0)
    private double ceilingHeight;
    @Size(max = 512)
    private String documents;
    @NotNull
    private boolean isPrivate;

}
