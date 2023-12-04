package it.nsis.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;


/**
 * @author mirco.cennamo on 20/10/2023
 * @project spring-boot-kafka-producer
 */
@SpringBootApplication
@EnableRetry

public class ProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProducerApplication.class, args);
	}




}
