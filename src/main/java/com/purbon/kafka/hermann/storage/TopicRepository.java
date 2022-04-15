package com.purbon.kafka.hermann.storage;

import com.purbon.kafka.hermann.model.Topic;
import org.springframework.data.repository.CrudRepository;

public interface TopicRepository extends CrudRepository<Topic, String> {
}
