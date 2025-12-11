package com.makurohashami.realtorconnect.repository;

import com.makurohashami.realtorconnect.entity.realtor.Contact;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    List<Contact> findAllByRealtorId(long realtorId);

}
