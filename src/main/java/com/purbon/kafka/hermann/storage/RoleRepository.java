package com.purbon.kafka.hermann.storage;

import com.purbon.kafka.hermann.model.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, String> {
}
