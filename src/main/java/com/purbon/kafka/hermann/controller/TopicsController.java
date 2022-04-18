package com.purbon.kafka.hermann.controller;

import com.purbon.kafka.hermann.controller.exceptions.ResourceNotFoundException;
import com.purbon.kafka.hermann.controller.request.ArtefactRequest;
import com.purbon.kafka.hermann.controller.request.TopicSpec;
import com.purbon.kafka.hermann.controller.response.TopicResponse;
import com.purbon.kafka.hermann.managers.TopicManager;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/v1/")
public class TopicsController {

    private TopicManager topicManager;

    public TopicsController(TopicManager topicManager) {
        this.topicManager = topicManager;
    }

    @PutMapping(
            value = "/topics/{name}",
            consumes = { "application/yaml", "application/yaml"},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public TopicResponse createOrUpdate(@PathVariable(value = "name") String name,
                                        @RequestBody ArtefactRequest<TopicSpec> requestBody) throws IOException {
        var response = topicManager.apply(requestBody, name);
        return response;
    }

    @GetMapping("/topics/{name}")
    public TopicResponse get(@PathVariable(value = "name") String name) {
        return topicManager.find(name).orElseThrow(ResourceNotFoundException::new);
    }

    @GetMapping("/topics")
    public List<TopicResponse> getAll() {
        return topicManager.all();
    }

    @DeleteMapping("/topics/{name}")
    public Optional<TopicResponse> delete(@PathVariable(value = "name") String name) throws IOException {
        return topicManager.delete(name);
    }
}
