package com.purbon.kafka.hermann.controller.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
public abstract class AclSpec implements ArtefactSpec {

    protected String principal;
    protected Map<String, String> metadata = new HashMap<>();
}
