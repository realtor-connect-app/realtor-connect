package com.makurohashami.realtorconnect.entity.realtor;

import com.makurohashami.realtorconnect.entity.realestate.RealEstate;
import com.makurohashami.realtorconnect.entity.realtor.enumeration.SubscriptionType;
import com.makurohashami.realtorconnect.entity.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "realtors_info")
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Realtor extends User {

    private String agency;
    private String agencySite;
    @OneToMany(mappedBy = "realtor", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @OrderBy("type asc")
    private Set<Contact> contacts = new HashSet<>();
    @OneToMany(mappedBy = "realtor", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @OrderBy("id asc")
    @EqualsAndHashCode.Exclude
    private Set<RealEstate> realEstates = new HashSet<>();
    private SubscriptionType subscriptionType;
    private int publicRealEstatesCount;
    private Instant premiumExpiresAt;
    @Column(name = "notified_days_to_expire_prem")
    private Integer notifiedDaysToExpirePremium;

}
