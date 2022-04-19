package com.example.springbootkafka.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xuqi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public final class PracticalAdvice {

    @JsonProperty("message")
    private String message;

    @JsonProperty("identifier")
    private int identifier;
}