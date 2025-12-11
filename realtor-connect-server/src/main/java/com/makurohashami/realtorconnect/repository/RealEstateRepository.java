package com.makurohashami.realtorconnect.repository;

import com.makurohashami.realtorconnect.entity.realestate.RealEstate;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RealEstateRepository extends JpaRepository<RealEstate, Long> {

    Page<RealEstate> findAll(Specification<RealEstate> spec, Pageable pageable);

    List<RealEstate> findAllByCalledAtBeforeAndCalledTrue(Instant instant);

    @Modifying
    @Query("UPDATE RealEstate re SET re.isPrivate = true WHERE re.isPrivate = false AND re.realtor.id IN :realtorIds")
    void makeAllRealEstatesPrivateByRealtors(List<Long> realtorIds);

}
