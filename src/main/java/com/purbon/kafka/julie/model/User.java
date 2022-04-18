package com.purbon.kafka.julie.model;

import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.IndexColumn;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
@Setter
@Getter
public class User {

  @Id
  @Column(name = "`name`")
  private String name;

  @Column(name ="`principal`")
  private String principal;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "service_account_metadata_mapping",
          joinColumns = {@JoinColumn(name = "user_name", referencedColumnName = "name")})
  @Column(name = "metadata")
  private Map<String, String> metadata;

  public User(String principal) {
    this.principal = principal;
  }
}
