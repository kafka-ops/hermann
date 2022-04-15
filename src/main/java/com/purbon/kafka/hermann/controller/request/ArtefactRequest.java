package com.purbon.kafka.hermann.controller.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ArtefactRequest<T extends ArtefactSpec> {

    private String namespace;
    private T spec;
}
