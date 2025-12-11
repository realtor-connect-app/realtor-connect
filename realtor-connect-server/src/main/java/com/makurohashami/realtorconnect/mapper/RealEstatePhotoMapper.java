package com.makurohashami.realtorconnect.mapper;

import com.makurohashami.realtorconnect.dto.realestate.photo.RealEstatePhotoDto;
import com.makurohashami.realtorconnect.dto.realestate.photo.RealEstatePhotoUpdateDto;
import com.makurohashami.realtorconnect.entity.realestate.RealEstatePhoto;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RealEstatePhotoMapper {

    RealEstatePhotoDto toDto(RealEstatePhoto photo);

    List<RealEstatePhotoDto> toListDto(List<RealEstatePhoto> photos);

    RealEstatePhoto update(@MappingTarget RealEstatePhoto toUpdate, RealEstatePhotoUpdateDto dto);

}
