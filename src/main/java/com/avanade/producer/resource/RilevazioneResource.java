package com.avanade.producer.resource;

import com.avanade.model.Rilevazione;
import com.avanade.producer.SenderAsyncCallBack;
import com.avanade.producer.SenderSyncCallBack;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ExecutionException;


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

    @Autowired
    private SenderSyncCallBack senderSyncCallBack;


    @PostMapping(path = "/publish/nuovaRilevazioneCallBack")
    @Retryable(retryFor = RuntimeException.class, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}",multiplier = 2))
    public ResponseEntity<Rilevazione> createCallBack(@RequestBody Rilevazione rilevazione){

        UUID uuid = UUID.randomUUID();
        rilevazione.setUuid(uuid);
        rilevazione.setInstant(Instant.now());
        log.info("Called RilevazioneResource createCallBack ::: body {} " + rilevazione.toString());
        if(log.isDebugEnabled()){
            log.debug(" method createCallBack has been called {}  ", Thread.currentThread());
            log.debug("UUID generated - {}  - UUID Version ", uuid, uuid.version());
        }
        senderAsyncCallBack.sendMessage(rilevazione, topic);
        return new ResponseEntity<>(rilevazione, HttpStatus.OK);
    }


    @PostMapping(path = "/publish/nuovaRilevazione")
    @Retryable(retryFor = RuntimeException.class, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}",multiplier = 2))
    public ResponseEntity<Rilevazione> create(@RequestBody Rilevazione rilevazione){

        UUID uuid = UUID.randomUUID();
        rilevazione.setUuid(uuid);
        rilevazione.setInstant(Instant.now());
        log.info("Called RilevazioneResource create ::: body {} " + rilevazione.toString());
        if(log.isDebugEnabled()){
            log.debug(" method create has been called {}  ", Thread.currentThread());
            log.debug("UUID generated - {}  - UUID Version ", uuid, uuid.version());
        }
        senderSyncCallBack.sendMessage(rilevazione, topic);
        return new ResponseEntity<>(rilevazione, HttpStatus.OK);
    }

    @Recover
    public  ResponseEntity<Rilevazione> publishFallback(Exception e,Rilevazione rilevazione){
        log.error("error send to kafka  bean {}  ::: error {} " , rilevazione, e.getMessage());
        return new ResponseEntity<>(rilevazione, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
