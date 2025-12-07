package com.makurohashami.realtorconnect.enumconverter;

import com.makurohashami.realtorconnect.entity.realestate.enumeration.AnnouncementType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AnnouncementTypeConverter implements AttributeConverter<AnnouncementType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(AnnouncementType attribute) {
        return attribute.getTypeId();
    }

    @Override
    public AnnouncementType convertToEntityAttribute(Integer dbData) {
        return AnnouncementType.getById(dbData);
    }
}
