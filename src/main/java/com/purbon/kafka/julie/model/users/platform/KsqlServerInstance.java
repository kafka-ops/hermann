package com.purbon.kafka.julie.model.users.platform;


import com.purbon.kafka.julie.model.User;

public class KsqlServerInstance extends User {

  private String ksqlDbId;
  private String owner;

  public KsqlServerInstance() {
    super("");
  }

  public String getKsqlDbId() {
    return ksqlDbId;
  }

  public String getOwner() {
    return owner;
  }

  public String commandTopic() {
    return String.format("_confluent-ksql-%s_command_topic", ksqlDbId);
  }

  public String internalTopics() {
    return String.format("_confluent-ksql-%s", ksqlDbId);
  }

  public String processingLogTopic() {
    return String.format("%sksql_processing_log", ksqlDbId);
  }

  public String consumerGroupPrefix() {
    return String.format("_confluent-ksql-%s", ksqlDbId);
  }

  public String TransactionId() {
    return String.format("TransactionId:%s", ksqlDbId);
  }
}
