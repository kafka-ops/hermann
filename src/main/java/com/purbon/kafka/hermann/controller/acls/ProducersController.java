package com.purbon.kafka.hermann.controller.acls;

import com.purbon.kafka.hermann.AccessControlProviderFactoryBean;
import com.purbon.kafka.hermann.BindingsProviderFactoryBean;
import com.purbon.kafka.hermann.controller.request.acl.ProducerSpec;
import com.purbon.kafka.hermann.storage.AclsRepository;
import com.purbon.kafka.julie.model.users.Producer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/acls/producers")
public class ProducersController extends AbstractAclController<ProducerSpec, Producer> {

    public ProducersController(AccessControlProviderFactoryBean accessControlProviderFactoryBean,
                               BindingsProviderFactoryBean bindingsProviderFactoryBean,
                               AclsRepository<Producer> repository
    ) {
        super(accessControlProviderFactoryBean, bindingsProviderFactoryBean, repository);
    }
}
