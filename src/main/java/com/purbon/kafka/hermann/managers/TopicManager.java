package com.purbon.kafka.hermann.managers;

import com.purbon.kafka.hermann.api.HermannAdminClient;
import com.purbon.kafka.hermann.controller.request.ArtefactRequest;
import com.purbon.kafka.hermann.controller.request.TopicSpec;
import com.purbon.kafka.hermann.controller.response.TopicResponse;
import com.purbon.kafka.hermann.model.Topic;
import com.purbon.kafka.hermann.storage.TopicRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class TopicManager {

    public static final String REPLICATION_FACTOR = "replication.factor";
    public static final String NUM_PARTITIONS = "num.partitions";

    private TopicRepository topicRepository;
    private HermannAdminClient adminClient;

    public TopicManager(TopicRepository topicRepository, HermannAdminClient adminClient) {
        this.topicRepository = topicRepository;
        this.adminClient = adminClient;
    }

    public TopicResponse apply(ArtefactRequest<TopicSpec> request, String name) throws IOException {

        var spec = request.getSpec();

        Topic t = new Topic();
        t.setName(String.format("%s.%s", request.getNamespace(), name));
        t.setConfig(spec.getConfig());
        t.setPartitions(spec.getPartitions());
        t.setReplicationFactor(spec.getReplicationFactor());

        var response = find(name);

        if (response.isPresent()) {// update config
            adminClient.updateTopicConfig(t);
        } else { // create from new
            adminClient.createTopic(t);
        }

        topicRepository.save(t);
        return new TopicResponse(t);
    }

    public boolean delete(String name) throws IOException {
        Optional<Topic> topic = topicRepository.findById(name);
        if (topic.isPresent()) {
            adminClient.deleteTopics(Collections.singletonList(name));
            topicRepository.delete(topic.get());
        }
        return true;
    }

    public List<TopicResponse> all() {
        List<TopicResponse> topics = new ArrayList<>();
        for(Topic topic : topicRepository.findAll()) {
            topics.add(new TopicResponse(topic));
        }
        return topics;
    }

    public Optional<TopicResponse> find(String name) {
        var optionalTopic = topicRepository.findById(name);
        return optionalTopic.map(TopicResponse::new);
    }
}
