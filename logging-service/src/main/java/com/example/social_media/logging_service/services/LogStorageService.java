package com.example.social_media.logging_service.services;

import com.example.social_media.shared_libs.models.LogEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LogStorageService {

    // TODO: replace with DB/ES repository
    public void store(LogEvent event) {
        log.info("Storing log: {}", event.getId());
    }
}
