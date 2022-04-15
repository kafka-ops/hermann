package com.purbon.kafka.hermann.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
public class TopicSpec implements ArtefactSpec {

    private String name;

    private Long partitions;
    @JsonProperty("replication.factor")
    private Short replicationFactor;

    private Map<String, String> config;
}
