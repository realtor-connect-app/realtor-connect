package com.makurohashami.realtorconnect.enumconverter.userrealtor;

import com.makurohashami.realtorconnect.entity.user.Role;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Role, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Role attribute) {
        return attribute.getRoleId();
    }

    @Override
    public Role convertToEntityAttribute(Integer roleId) {
        return Role.getById(roleId);
    }
}
