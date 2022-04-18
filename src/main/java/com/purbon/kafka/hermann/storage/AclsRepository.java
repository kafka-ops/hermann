package com.purbon.kafka.hermann.storage;

import com.purbon.kafka.julie.model.User;
import org.springframework.data.repository.CrudRepository;

public interface AclsRepository<U extends User> extends CrudRepository<U, String> {
}
