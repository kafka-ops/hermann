package com.purbon.kafka.hermann.model;

import com.purbon.kafka.julie.model.SubjectNameStrategy;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import java.util.Map;

import static com.purbon.kafka.julie.model.SubjectNameStrategy.TOPIC_NAME_STRATEGY;

@Entity
@NoArgsConstructor
@Setter
@Getter
public class Topic {

    @Id
    private String name;

    private Long partitions;
    private Short replicationFactor;

    @Enumerated(EnumType.STRING)
    private SubjectNameStrategy subjectNameStrategy = TOPIC_NAME_STRATEGY;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "topic_config_mapping",
            joinColumns = {@JoinColumn(name = "topic_name", referencedColumnName = "name")})
    @Column(name = "config")
    private Map<String, String> config;
}
