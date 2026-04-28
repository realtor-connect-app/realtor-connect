package com.makurohashami.realtorconnect.enumconverter.userrealtor;

import com.makurohashami.realtorconnect.entity.realtor.enumeration.SubscriptionType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SubscriptionTypeConverter implements AttributeConverter<SubscriptionType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(SubscriptionType attribute) {
        return attribute.getSubscriptionId();
    }

    @Override
    public SubscriptionType convertToEntityAttribute(Integer subscriptionId) {
        return SubscriptionType.getById(subscriptionId);
    }

}
