package com.avanade.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author mirco.cennamo on 22/11/2023
 * @project spring-boot-kafka-producerOnWebLogic
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location {
    private String latitude;
    private String longitude;



}
