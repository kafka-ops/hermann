package com.purbon.kafka.julie.model.users.platform;

import com.purbon.kafka.julie.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class KafkaConnect {

  private Optional<Map<String, List<User>>> rbac;

  public KafkaConnect() {
    rbac = Optional.empty();
  }

  public Optional<Map<String, List<User>>> getRbac() {
    return rbac;
  }

  public void setRbac(Optional<Map<String, List<User>>> rbac) {
    this.rbac = rbac;
  }
}
