package com.makurohashami.realtorconnect.dto.realtor;

import com.makurohashami.realtorconnect.dto.user.UserFullDto;
import com.makurohashami.realtorconnect.entity.realtor.enumeration.SubscriptionType;
import java.time.Instant;
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
public class RealtorFullDto extends UserFullDto {

    private String agency;
    private String agencySite;
    private SubscriptionType subscriptionType;
    private Instant premiumExpiresAt;
    private List<ContactDto> contacts = new ArrayList<>();

}
