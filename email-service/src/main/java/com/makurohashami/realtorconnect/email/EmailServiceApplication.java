package com.makurohashami.realtorconnect.email;

import java.time.LocalDateTime;
import java.util.TimeZone;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@EnableAspectJAutoProxy
@SpringBootApplication
public class EmailServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmailServiceApplication.class, args);
        changeDefaultTimeZone();
    }

    private static void changeDefaultTimeZone() {
        log.info("Setting system timezone to UTC...");
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        log.info("Current system timezone is now {}", TimeZone.getDefault().getID());
        log.info("Current system time is {}", LocalDateTime.now());
    }

}
