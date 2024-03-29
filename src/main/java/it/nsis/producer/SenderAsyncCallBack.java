/*
 * Copyright (c) 2023. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package it.nsis.producer;

import brave.Span;
import brave.Tracer;
import io.micrometer.tracing.annotation.NewSpan;
import io.micrometer.tracing.annotation.SpanTag;
import it.nsis.model.Rilevazione;
import it.nsis.utility.TagConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

/**
 * @author mirco.cennamo on 20/10/2023
 * @project spring-boot-kafka-producer
 */
@Configuration
public class SenderAsyncCallBack {
    @Autowired
    private Tracer tracer;

    private KafkaTemplate<String, Rilevazione> kafkaTemplate;


    @Autowired
    SenderAsyncCallBack(KafkaTemplate<String, Rilevazione> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;

    }


    @NewSpan("sendMessage")
    public CompletableFuture<SendResult<String, Rilevazione>> sendMessage(@SpanTag("rilevazione.request")Rilevazione rilevazione, String topicName) {
        Span span = this.tracer.currentSpan();
        span.tag(TagConst.CORRELATION_ID, rilevazione.getUuid().toString());
        span.tag(TagConst.TARGA, rilevazione.getLicensePlate());
        return kafkaTemplate.send(topicName, rilevazione);
    }
}
