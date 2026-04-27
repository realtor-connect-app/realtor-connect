package com.makurohashami.realtorconnect.repository;

import com.makurohashami.realtorconnect.entity.realestate.RealEstatePhoto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RealEstatePhotoRepository extends JpaRepository<RealEstatePhoto, Long> {

    List<RealEstatePhoto> findAllByRealEstateId(long realEstateId);

}
