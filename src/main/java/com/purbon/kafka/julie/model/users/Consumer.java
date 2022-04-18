package com.purbon.kafka.julie.model.users;

import com.purbon.kafka.julie.model.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "consumer")
@Data
@EqualsAndHashCode
public class Consumer extends User {

  @Column(name ="`group`")
  private String group;

  public Consumer() {
    super("");
    this.group = "*";
  }

  public Consumer(String principal) {
    this(principal, "*");
  }

  public Consumer(String principal, String group) {
    super(principal);
    this.group = group;
  }

}
