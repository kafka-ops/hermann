package com.purbon.kafka.hermann.managers;

import com.purbon.kafka.hermann.controller.request.ArtefactRequest;
import com.purbon.kafka.hermann.controller.request.ArtefactSpec;

import java.io.IOException;
import java.util.Optional;

public interface ArtefactManager<S extends ArtefactSpec,R> {

    R apply(ArtefactRequest<S> request, String name) throws IOException;

    Optional<R> delete(String name) throws IOException;
}
