package it.nsis.producer.config;

import brave.kafka.interceptor.TracingProducerInterceptor;
import it.nsis.model.Rilevazione;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;


/**
 * @author mirco.cennamo on 20/10/2023
 * @project spring-boot-kafka-producer
 * classe di configurazione dove sono censiti due tipologie di kafkaTemplate
 * uno con listener su invio e l'altro senza
 */
@Configuration
@Slf4j
public class KakfaConfiguration {

    @Value("${bootstrap.servers}")
    private String bootstrapServers;

    @Value("${ack.config}")
    private String acks;



    @Value("${batch.size.config}")
    private Integer batchSize;

    @Value("${linger.ms.config}")
    private Integer lingerMs;

    @Value("${buffer.memory.config}")
    private Integer bufferMemory;


    @Value("${zipkin.http.endpoint}")
    private String zipkinHttpEndPoint;






    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, acks); //all
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, "120000");


        //TODO da definire il batch size
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        props.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);


        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, TracingProducerInterceptor.class.getName());
        props.put("zipkin.http.endpoint", zipkinHttpEndPoint);
        props.put("zipkin.sender.type", "HTTP");
        props.put("zipkin.encoding", "JSON");
        props.put("zipkin.remote.service.name", "scntt-kafka");
        props.put("zipkin.local.service.name", "scntt-kafka");
        props.put("zipkin.trace.id.128bit.enabled", "true");
        props.put("zipkin.sampler.rate", "1.0F");
        return props;
    }


    @Bean
    public ProducerFactory<String, Rilevazione> producerFactory() {
      return new DefaultKafkaProducerFactory<>(producerConfigs());
    }




    @Bean(name="templateKafka")
    public KafkaTemplate<String, Rilevazione> kafkaTemplateAsyncCallBack() {
            KafkaTemplate kt = new KafkaTemplate<>(producerFactory());
            kt.setProducerListener(new ProducerListener() {

            @Override
            public void onSuccess(ProducerRecord record, RecordMetadata recordMetadata) {
                if(log.isDebugEnabled()){
                    log.debug("Message = {}  sended  on topic {} ; partition = {} with offset= {} ; Timestamp : {} ; Message Size = {}" ,
                            record.value(),     recordMetadata.topic() , recordMetadata.partition() , recordMetadata.offset()
                            , recordMetadata.timestamp() , recordMetadata.serializedValueSize());
                }

            }

            @Override
            public void onError(ProducerRecord producerRecord, @Nullable RecordMetadata recordMetadata, Exception exception) {
                if(log.isDebugEnabled()){
                    log.debug("error send message on Topic = {} ; Message = {} ; exception = {} " ,
                            producerRecord.topic() , producerRecord.value() , exception.getMessage());
                }
                exception.printStackTrace();
            }
        });
        return kt;
    }




}

