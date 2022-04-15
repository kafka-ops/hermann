package com.purbon.kafka.hermann.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import java.util.Map;

@Entity
@NoArgsConstructor
@Setter
@Getter
public class Topic {

    @Id
    private String name;

    private Long partitions;
    private Short replicationFactor;

    @ElementCollection
    @CollectionTable(name = "topic_config_mapping",
            joinColumns = {@JoinColumn(name = "topic_name", referencedColumnName = "name")})
    @Column(name = "config")
    private Map<String, String> config;
}
