package it.nsis.producer.resource;

import brave.Span;
import brave.Tracer;
import it.nsis.model.Rilevazione;
import it.nsis.producer.SenderAsyncCallBack;
import it.nsis.producer.SenderSyncCallBack;
import it.nsis.utility.TagConst;
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


/**
 * @author mirco.cennamo on 20/10/2023
 * @project spring-boot-kafka-producer
 */
@RestController
@RequestMapping("nsis")
@Slf4j
public class RilevazioneResource {

    @Value("${spring.kafka.topic.name}")
    private String topic;

    @Autowired
    private SenderAsyncCallBack senderAsyncCallBack;

    @Autowired
    private SenderSyncCallBack senderSyncCallBack;

    @Autowired
    private  Tracer tracer;


    @PostMapping(path = "scntt/publish/new")
    @Retryable(retryFor = RuntimeException.class, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}",multiplier = 2))
    public ResponseEntity<Rilevazione> createCallBack(@RequestBody Rilevazione rilevazione){
        Span span = this.tracer.currentSpan();
        UUID uuid = UUID.randomUUID();
        rilevazione.setUuid(uuid);
       // rilevazione.setInstant(Instant.now());
        span.tag(TagConst.CORRELATION_ID, uuid.toString());
        span.tag(TagConst.TARGA, rilevazione.getLicensePlate());
        span.tag(TagConst.MESSAGGIO, rilevazione.toString());


        log.info("Called RilevazioneResource createCallBack ::: body {} " + rilevazione);
        if(log.isDebugEnabled()){
            log.debug("method createCallBack has been called {} :::  UUID generated  {}  ::: targa {} ",Thread.currentThread(), uuid,rilevazione.getLicensePlate());
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
       // rilevazione.setInstant(Instant.now());
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
