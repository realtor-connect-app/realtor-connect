package com.makurohashami.realtorconnect;

import java.time.LocalDateTime;
import java.util.TimeZone;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class RealtorConnectApplication {

    public static void main(String[] args) {
        changeDefaultTimeZone();
        SpringApplication.run(RealtorConnectApplication.class, args);
    }

    private static void changeDefaultTimeZone() {
        log.info("Setting system timezone to UTC...");
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        log.info("Current system timezone is now {}", TimeZone.getDefault().getID());
        log.info("Current system time is {}", LocalDateTime.now());
    }

}
