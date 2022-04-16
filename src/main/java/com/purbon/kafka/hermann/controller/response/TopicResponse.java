package com.purbon.kafka.hermann.controller.response;


import com.purbon.kafka.hermann.model.Topic;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
public class TopicResponse {

    private String name;
    private Map<String, String> config;

    public TopicResponse(Topic topic) {
        this.name = topic.getName();
        this.config = topic.getConfig();
    }
}
