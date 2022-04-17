package com.purbon.kafka.hermann.storage;

import com.purbon.kafka.hermann.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {
}
