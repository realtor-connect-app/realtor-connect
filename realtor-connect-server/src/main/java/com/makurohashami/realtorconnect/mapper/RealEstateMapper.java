package com.makurohashami.realtorconnect.mapper;

import com.makurohashami.realtorconnect.dto.realestate.RealEstateAddDto;
import com.makurohashami.realtorconnect.dto.realestate.RealEstateDto;
import com.makurohashami.realtorconnect.dto.realestate.RealEstateFullDto;
import com.makurohashami.realtorconnect.entity.realestate.RealEstate;
import com.makurohashami.realtorconnect.entity.realtor.Realtor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface RealEstateMapper {

    @Named("fromId")
    default Realtor fromId(Long id) {
        if (id == null) return null;
        return Realtor.builder().id(id).build();
    }

    @Mapping(source = "realtor.id", target = "realtorId")
    RealEstateDto toDto(RealEstate realEstate);

    @Mapping(source = "realtor.id", target = "realtorId")
    RealEstateFullDto toFullDto(RealEstate realEstate);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "realtorId", target = "realtor", qualifiedByName = "fromId")
    @Mapping(target = "verified", constant = "false")
    @Mapping(target = "called", constant = "true")
    @Mapping(target = "calledAt", expression = "java( java.time.Instant.now() )")
    RealEstate toEntity(RealEstateAddDto dto, long realtorId);

    @Mapping(target = "id", ignore = true)
    RealEstate update(@MappingTarget RealEstate toUpdate, RealEstateAddDto dto);
}
