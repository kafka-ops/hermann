package com.purbon.kafka.hermann.controller.acls;

import com.purbon.kafka.hermann.AccessControlProviderFactoryBean;
import com.purbon.kafka.hermann.BindingsProviderFactoryBean;
import com.purbon.kafka.hermann.controller.request.AclSpec;
import com.purbon.kafka.hermann.controller.request.ArtefactRequest;
import com.purbon.kafka.hermann.controller.response.AclResponse;
import com.purbon.kafka.hermann.managers.AccessControlManager;
import com.purbon.kafka.hermann.storage.AclsRepository;
import com.purbon.kafka.julie.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.Optional;

public class AbstractAclController<S extends AclSpec, U extends User> {

    private static final Logger LOGGER = LogManager.getLogger(AbstractAclController.class);

    private AccessControlManager<S, U> aclManager;
    private AclsRepository<U> repository;

    public AbstractAclController(AccessControlProviderFactoryBean accessControlProviderFactoryBean,
                                 BindingsProviderFactoryBean bindingsProviderFactoryBean,
                                 AclsRepository<U> repository) {
        try {
            this.aclManager = new AccessControlManager<>(
                    accessControlProviderFactoryBean.getObject(),
                    bindingsProviderFactoryBean.getObject(),
                    repository
            );
            this.repository = repository;
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    @PutMapping(
            value = "",
            consumes = { "application/yaml", "application/yaml"},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public AclResponse<U> createOrUpdate(@RequestBody ArtefactRequest<S> requestBody) throws IOException {
        return aclManager.apply(requestBody, requestBody.getName());
    }

    @DeleteMapping(
            value = "/{name}",
            consumes = { "application/yaml", "application/yaml"},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Optional<AclResponse> delete(@PathVariable String name) throws IOException {
        return aclManager.delete(name);
    }
}
