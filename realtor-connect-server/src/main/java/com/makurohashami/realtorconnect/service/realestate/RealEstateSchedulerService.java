package com.makurohashami.realtorconnect.service.realestate;

import com.makurohashami.realtorconnect.conditions.SchedulerEnabled;
import com.makurohashami.realtorconnect.config.RealEstateConfiguration;
import com.makurohashami.realtorconnect.entity.realestate.RealEstate;
import com.makurohashami.realtorconnect.repository.RealEstateRepository;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Conditional(SchedulerEnabled.class)
public class RealEstateSchedulerService {

    private final RealEstateRepository realEstateRepository;
    private final RealEstateConfiguration realEstateConfiguration;

    @Transactional
    @Scheduled(fixedRateString = "${real-estate.scheduler.check-called}")
    public void setNotCalledWhenCalledAtExpired() {
        Instant time = ZonedDateTime.now().minusDays(realEstateConfiguration.getDaysForExpireCalled()).toInstant();
        List<RealEstate> realEstates = realEstateRepository.findAllByCalledAtBeforeAndCalledTrue(time);
        realEstates.forEach(realEstate -> realEstate.setCalled(false));
    }

}
