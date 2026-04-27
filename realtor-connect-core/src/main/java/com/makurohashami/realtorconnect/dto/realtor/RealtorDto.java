package com.makurohashami.realtorconnect.dto.realtor;

import com.makurohashami.realtorconnect.dto.user.UserDto;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RealtorDto extends UserDto {

    private String agency;
    private String agencySite;
    private List<ContactDto> contacts = new ArrayList<>();

}
