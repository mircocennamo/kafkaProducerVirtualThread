package it.nsis.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;


/**
 * @author mirco.cennamo on 20/10/2023
 * @project scntt-kafka-producer
 */
@SpringBootApplication
@EnableRetry

public class ScnttKafkaProducer {

	public static void main(String[] args) {
		SpringApplication.run(ScnttKafkaProducer.class, args);
	}




}
