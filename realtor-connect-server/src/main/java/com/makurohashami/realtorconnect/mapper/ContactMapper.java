package com.makurohashami.realtorconnect.mapper;

import com.makurohashami.realtorconnect.dto.realtor.ContactDto;
import com.makurohashami.realtorconnect.entity.realtor.Contact;
import com.makurohashami.realtorconnect.entity.realtor.Realtor;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ContactMapper {

    @Named("fromId")
    default Realtor fromId(Long id) {
        if (id == null) return null;
        return Realtor.builder().id(id).build();
    }

    ContactDto toDto(Contact contact);

    List<ContactDto> toListDto(List<Contact> contacts);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "realtorId", target = "realtor", qualifiedByName = "fromId")
    Contact toEntity(ContactDto dto, long realtorId);

    @Mapping(target = "id", ignore = true)
    Contact update(@MappingTarget Contact contact, ContactDto dto);

}
