package com.purbon.kafka.julie.model.users;

import com.purbon.kafka.julie.model.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;

@Entity(name = "producer")
@NoArgsConstructor
@ToString
@Data
@EqualsAndHashCode
public class Producer extends User {

  String transactionId;
  Boolean idempotence;

  public Producer(String principal) {
    this(principal, "", false);
  }

  public Producer(String principal, String transactionId, Boolean idempotence) {
    super(principal);
    this.transactionId = transactionId;
    this.idempotence = idempotence;
  }
}
