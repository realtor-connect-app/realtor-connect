package com.makurohashami.realtorconnect.email.service;

import com.makurohashami.realtorconnect.email.dao.EmailDAO;
import com.makurohashami.realtorconnect.email.model.EmailMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailProcessorService {

    private final EmailDAO emailDAO;
    private final EmailSenderService emailSenderService;

    public void addToQueue(EmailMessage emailMessage) {

    }

    @Scheduled()
    protected void processEmails() {

    }

}
