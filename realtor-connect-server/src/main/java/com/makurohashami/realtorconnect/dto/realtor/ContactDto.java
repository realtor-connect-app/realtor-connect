package com.makurohashami.realtorconnect.dto.realtor;

import com.makurohashami.realtorconnect.entity.realtor.enumeration.ContactType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactDto {

    private Long id;
    private ContactType type;
    private String contact;

}
