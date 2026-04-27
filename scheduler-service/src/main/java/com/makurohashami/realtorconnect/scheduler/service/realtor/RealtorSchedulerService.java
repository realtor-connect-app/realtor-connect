package com.makurohashami.realtorconnect.scheduler.service.realtor;

import com.makurohashami.realtorconnect.config.RealtorConfiguration;
import com.makurohashami.realtorconnect.entity.realtor.Realtor;
import com.makurohashami.realtorconnect.entity.realtor.enumeration.SubscriptionType;
import com.makurohashami.realtorconnect.repository.RealEstateRepository;
import com.makurohashami.realtorconnect.repository.RealtorRepository;
import com.makurohashami.realtorconnect.service.email.EmailService;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RealtorSchedulerService {

    private final RealtorRepository realtorRepository;
    private final RealEstateRepository realEstateRepository;
    private final EmailService emailService;
    private final RealtorConfiguration realtorConfiguration;

    @Transactional
    @Scheduled(cron = "${realtor.scheduler.reset-plan-cron}")
    public void setFreeSubscriptionWhenPrivateExpired() {
        List<Realtor> realtors = realtorRepository.findAllByPremiumExpiresAtBeforeAndSubscriptionType(Instant.now(), SubscriptionType.PREMIUM);
        realtors.forEach(realtor -> {
            realtor.setPremiumExpiresAt(null);
            realtor.setNotifiedDaysToExpirePremium(null);
            realtor.setSubscriptionType(SubscriptionType.FREE);
            log.debug("Reset subscription for realtor: '{}'", realtor.getId());
        });
        List<Long> realtorIds = realtors.stream().map(Realtor::getId).toList();
        realEstateRepository.makeAllRealEstatesPrivateByRealtors(realtorIds);
        realtors.forEach(emailService::sendPremiumExpired);
    }

    @Transactional
    @Scheduled(cron = "${realtor.scheduler.send-email-when-premium-expires-cron}")
    public void sendEmailWhenLeftOneDayOfPremium() {
        realtorConfiguration.getDaysToNotifyExpiresPremium().forEach(
                daysLeft -> CompletableFuture.runAsync(() -> sendEmailWhenLeftFewDaysOfPremium(daysLeft))
        );
    }

    protected void sendEmailWhenLeftFewDaysOfPremium(int daysLeft) {
        ZonedDateTime time = ZonedDateTime.now().plusDays(daysLeft);
        realtorRepository.findAllNotNotifiedExpiringPremium(daysLeft, time.getDayOfMonth(), time.getMonthValue(), time.getYear())
                .forEach(realtor -> {
                    emailService.sendPremiumExpires(realtor);
                    realtor.setNotifiedDaysToExpirePremium(daysLeft);
                    realtorRepository.save(realtor);
                });
    }

}
