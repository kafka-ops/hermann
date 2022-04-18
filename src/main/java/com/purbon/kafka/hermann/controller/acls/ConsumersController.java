package com.purbon.kafka.hermann.controller.acls;

import com.purbon.kafka.hermann.AccessControlProviderFactoryBean;
import com.purbon.kafka.hermann.BindingsProviderFactoryBean;
import com.purbon.kafka.hermann.controller.request.acl.ConsumerSpec;
import com.purbon.kafka.hermann.storage.AclsRepository;
import com.purbon.kafka.julie.model.users.Consumer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/acls/consumers")
public class ConsumersController extends AbstractAclController<ConsumerSpec, Consumer> {

    public ConsumersController(AccessControlProviderFactoryBean accessControlProviderFactoryBean,
                               BindingsProviderFactoryBean bindingsProviderFactoryBean,
                               AclsRepository<Consumer> repository) {
        super(accessControlProviderFactoryBean, bindingsProviderFactoryBean, repository);
    }
}
