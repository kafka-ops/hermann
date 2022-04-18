package com.purbon.kafka.hermann.managers;

import com.purbon.kafka.hermann.controller.request.AclSpec;
import com.purbon.kafka.hermann.controller.request.ArtefactRequest;
import com.purbon.kafka.hermann.controller.request.acl.ConsumerSpec;
import com.purbon.kafka.hermann.controller.request.acl.ProducerSpec;
import com.purbon.kafka.hermann.controller.response.AclResponse;
import com.purbon.kafka.hermann.storage.AclsRepository;
import com.purbon.kafka.julie.AccessControlProvider;
import com.purbon.kafka.julie.BindingsBuilderProvider;
import com.purbon.kafka.julie.model.User;
import com.purbon.kafka.julie.model.users.Consumer;
import com.purbon.kafka.julie.model.users.Producer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;

import static java.util.Collections.singletonList;

public class AccessControlManager<S extends AclSpec, U extends User> implements ArtefactManager<S, AclResponse> {

    private AccessControlProvider accessProvider;
    private BindingsBuilderProvider bindingsProvider;
    private AclsRepository<U> repository;

    public AccessControlManager(AccessControlProvider accessProvider,
                                BindingsBuilderProvider bindingsProvider,
                                AclsRepository<U> repository) {
        this.accessProvider = accessProvider;
        this.bindingsProvider = bindingsProvider;
        this.repository = repository;
    }

    @Override
    public AclResponse apply(ArtefactRequest<S> request, String name) throws IOException {

        var specOriginal = request.getSpec();
        AclResponse<U> response = null;

        if (specOriginal instanceof ConsumerSpec) {
            ConsumerSpec consumerSpec = (ConsumerSpec) specOriginal;
            response = apply(request, consumerSpec, request.getName());

        } else if (specOriginal instanceof ProducerSpec) {
            ProducerSpec producerSpec = (ProducerSpec) specOriginal;
            response = apply(request, producerSpec, request.getName());
        }
        if (response != null) {
            for (U entity : response.getEntity()) {
                repository.save(entity);
            }
            return response;
        }

        throw new IOException("Unknown request type "+specOriginal.getClass());
    }

    @Override
    public Optional<AclResponse> delete(String name) throws IOException {

        Optional<U> entityOptional = repository.findById(name);
        if (entityOptional.isPresent()) {
            if (entityOptional.get() instanceof Consumer) {
                Consumer c = (Consumer)entityOptional.get();
                var bindings = bindingsProvider.buildBindingsForConsumers(
                        singletonList(c),
                        "",
                        true
                );
                accessProvider.clearBindings(new HashSet<>(bindings));
            } else if (entityOptional.get() instanceof Producer) {

            }
        }

        return Optional.empty();
    }

    private AclResponse apply(ArtefactRequest<S> request, ConsumerSpec consumerSpec, String name) throws IOException {
        String topic = request.getNamespace();
        var consumers = singletonList(asConsumer(consumerSpec, name));
        var bindings = bindingsProvider.buildBindingsForConsumers(consumers, topic, true);
        accessProvider.createBindings(new HashSet<>(bindings));
        return new AclResponse<>(bindings, consumers);
    }

    private AclResponse apply(ArtefactRequest<S> request, ProducerSpec producerSpec, String name) throws IOException {
        String topic = request.getNamespace();
        var producers = singletonList(asProducer(producerSpec, name));
        var bindings = bindingsProvider.buildBindingsForProducers(producers, topic, true);
        accessProvider.createBindings(new HashSet<>(bindings));
        return new AclResponse(bindings, producers);
    }

    private Consumer asConsumer(ConsumerSpec spec, String name) {
        var consumer = new Consumer(spec.getPrincipal());
        consumer.setName(name);
        consumer.setGroup(spec.getGroup());
        consumer.setMetadata(spec.getMetadata());
        return consumer;
    }

    private Producer asProducer(ProducerSpec spec, String name) {
        var producer = new Producer(spec.getPrincipal());
        producer.setName(name);
        producer.setMetadata(spec.getMetadata());
        producer.setIdempotence(spec.getIdempotence());
        producer.setTransactionId(spec.getTransactionId());
        return producer;
    }
}
