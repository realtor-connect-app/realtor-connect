package com.makurohashami.realtorconnect.service.realtor;

import com.makurohashami.realtorconnect.annotation.Loggable;
import com.makurohashami.realtorconnect.annotation.datafilter.ContactsFiltered;
import com.makurohashami.realtorconnect.dto.realtor.RealtorAddDto;
import com.makurohashami.realtorconnect.dto.realtor.RealtorDto;
import com.makurohashami.realtorconnect.dto.realtor.RealtorFilter;
import com.makurohashami.realtorconnect.dto.realtor.RealtorFullDto;
import com.makurohashami.realtorconnect.entity.realtor.Realtor;
import com.makurohashami.realtorconnect.entity.realtor.enumeration.SubscriptionType;
import com.makurohashami.realtorconnect.mapper.RealtorMapper;
import com.makurohashami.realtorconnect.repository.RealtorRepository;
import com.makurohashami.realtorconnect.service.email.EmailService;
import com.makurohashami.realtorconnect.service.user.ConfirmationTokenService;
import com.makurohashami.realtorconnect.specification.RealtorFilterSpecifications;
import com.makurohashami.realtorconnect.util.exception.ResourceNotFoundException;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Loggable
@RequiredArgsConstructor
public class RealtorService {

    public static final String NOT_FOUND_BY_ID_MSG = "Realtor with id '%d' not found";

    private final EmailService emailService;
    private final RealtorMapper realtorMapper;
    private final RealtorRepository realtorRepository;
    private final ConfirmationTokenService tokenService;

    @Transactional
    @Counted(value = "realtorconnect.realtor.service")
    @Timed(value = "realtorconnect.realtor.service", histogram = true)
    public RealtorFullDto create(RealtorAddDto dto) {
        Realtor realtor = realtorRepository.save(realtorMapper.toEntity(dto));
        emailService.sendVerifyEmail(realtor, tokenService.createToken(realtor).toString());
        return realtorMapper.toFullDto(realtor);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "getRealtorFullDto", key = "#id")
    @Counted(value = "realtorconnect.realtor.service")
    @Timed(value = "realtorconnect.realtor.service", histogram = true)
    public RealtorFullDto readFullById(long id) {
        return realtorMapper.toFullDto(realtorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(NOT_FOUND_BY_ID_MSG, id))));
    }

    @ContactsFiltered
    @Transactional(readOnly = true)
    @Cacheable(value = "getRealtorDto", key = "#id")
    @Counted(value = "realtorconnect.realtor.service")
    @Timed(value = "realtorconnect.realtor.service", histogram = true)
    public RealtorDto readShortById(long id) {
        return realtorMapper.toDto(realtorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(NOT_FOUND_BY_ID_MSG, id))));
    }

    @ContactsFiltered
    @Cacheable(value = "getListRealtorDto", key = "#filter+'-'+#pageable")
    @Counted(value = "realtorconnect.permission.service")
    @Timed(value = "realtorconnect.permission.service", histogram = true)
    public Page<RealtorDto> getAllShorts(RealtorFilter filter, Pageable pageable) {
        Specification<Realtor> spec = RealtorFilterSpecifications.withFilter(filter);
        return realtorRepository.findAll(spec, pageable).map(realtorMapper::toDto);
    }

    @Transactional
    @Counted(value = "realtorconnect.realtor.service")
    @Timed(value = "realtorconnect.realtor.service", histogram = true)
    public RealtorFullDto update(long id, RealtorAddDto dto) {
        Realtor toUpdate = realtorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(NOT_FOUND_BY_ID_MSG, id)));
        return realtorMapper.toFullDto(realtorMapper.update(toUpdate, dto));
    }

    @Transactional
    public void delete(long id) {
        realtorRepository.deleteById(id);
    }

    @Transactional
    @Counted(value = "realtorconnect.realtor.service")
    @Timed(value = "realtorconnect.realtor.service", histogram = true)
    public Instant givePremiumToRealtor(long id, short durationInMonths) {
        Realtor realtor = realtorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(NOT_FOUND_BY_ID_MSG, id)));
        realtor.setSubscriptionType(SubscriptionType.PREMIUM);
        if (realtor.getPremiumExpiresAt() == null) {
            realtor.setPremiumExpiresAt(ZonedDateTime.now().withHour(0).withMinute(0)
                    .withSecond(0).withNano(0).plusDays(1).toInstant());
        }
        realtor.setPremiumExpiresAt(ZonedDateTime.ofInstant(realtor.getPremiumExpiresAt(), ZoneOffset.UTC)
                .plusMonths(durationInMonths).toInstant());
        realtor.setNotifiedDaysToExpirePremium(Integer.MAX_VALUE);
        realtorRepository.save(realtor);
        emailService.sendStartPremium(realtor, durationInMonths);
        return realtor.getPremiumExpiresAt();
    }

}
