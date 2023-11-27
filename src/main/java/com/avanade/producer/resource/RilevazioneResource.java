package com.avanade.producer.resource;

import com.avanade.model.Rilevazione;
import com.avanade.producer.SenderAsyncCallBack;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;


/**
 * @author mirco.cennamo on 20/10/2023
 * @project spring-boot-kafka-producer
 */
@RestController
@RequestMapping("avanade")
@Slf4j
public class RilevazioneResource {

    @Value("${spring.kafka.topic.name}")
    private String topic;

    @Autowired
    private SenderAsyncCallBack senderAsyncCallBack;


    @PostMapping(path = "/publish/nuovaRilevazioneCallBack")
    @Retryable(value= RuntimeException.class,maxAttempts = 3,backoff = @Backoff(delay=10000))
    public ResponseEntity<Rilevazione> createCallBack(@RequestBody Rilevazione rilevazione) {
        log.debug("RilevazioneResource method createCallBack has been called {}", Thread.currentThread());
        UUID uuid = UUID.randomUUID();
        log.debug("UUID generated - {}  - UUID Version ", uuid, uuid.version());
        rilevazione.setUuid(uuid);
        rilevazione.setInstant(Instant.now());
        senderAsyncCallBack.sendMessage(rilevazione, topic);
        return new ResponseEntity<>(rilevazione, HttpStatus.OK);
    }


}
