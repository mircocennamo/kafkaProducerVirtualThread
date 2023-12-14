package it.nsis.producer.resource;

import brave.Span;
import brave.Tracer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.nsis.model.Rilevazione;
import it.nsis.producer.SenderAsyncCallBack;
import it.nsis.utility.TagConst;
import it.nsis.viewmodel.RilevazioneRequestVm;
import it.nsis.viewmodel.RilevazioneResponseVm;
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

import java.util.Date;
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
    private  Tracer tracer;


    @Operation(summary = "publish a new message in cluster SCNTT in async mode")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "message published",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RilevazioneResponseVm.class)) }),
            @ApiResponse(responseCode = "500", description = "Error in message published",
                    content = @Content) })
    @PostMapping(path = "scntt/publish/new")
    @Retryable(retryFor = RuntimeException.class, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}",multiplier = 2))
    public ResponseEntity<RilevazioneResponseVm> createCallBack(@RequestBody RilevazioneRequestVm rilevazioneRequestVm){

        Rilevazione rilevazione = RilevazioneRequestVm.toModel(rilevazioneRequestVm);
        Span span = this.tracer.currentSpan();
        UUID uuid = UUID.randomUUID();
        rilevazione.setUuid(uuid);
        rilevazione.setInsertAt(new Date());

        span.tag(TagConst.CORRELATION_ID, uuid.toString());
        span.tag(TagConst.TARGA, rilevazione.getLicensePlate());
        span.tag(TagConst.MESSAGGIO, rilevazione.toString());


        log.info("Called RilevazioneResource createCallBack ::: body {} " + rilevazione);
        log.debug("Thread for createCallBack {} " , Thread.currentThread());
        if(log.isDebugEnabled()){
            log.debug("method createCallBack has been called {} :::  UUID generated  {}  ::: targa {} ",Thread.currentThread(), uuid,rilevazione.getLicensePlate());
        }

        senderAsyncCallBack.sendMessage(rilevazione, topic);
        return new ResponseEntity<>(RilevazioneResponseVm.fromModel(rilevazione), HttpStatus.OK);
    }






    @Recover
    public  ResponseEntity<RilevazioneRequestVm> publishFallback(Exception e,RilevazioneRequestVm rilevazioneRequestVm){
        log.debug("Thread for publishFallback {} " , Thread.currentThread());
        log.error("error send to kafka  bean {}  ::: error {} " , rilevazioneRequestVm, e.getMessage());
        return new ResponseEntity<>(rilevazioneRequestVm, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
