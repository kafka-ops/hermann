package com.purbon.kafka.hermann.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@Data
public class User {

    @Id
    private String username;
    private String password;

    private boolean admin;
}
