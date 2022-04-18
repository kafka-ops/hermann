package com.purbon.kafka.julie.roles;

import com.purbon.kafka.julie.AccessControlProvider;
import com.purbon.kafka.julie.api.JulieAdminClient;
import com.purbon.kafka.julie.model.JulieAclBinding;
import org.apache.kafka.common.acl.AclBinding;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SimpleAclsProvider implements AccessControlProvider {

    private static final Logger LOGGER = LogManager.getLogger(SimpleAclsProvider.class);

    private final JulieAdminClient adminClient;

    public SimpleAclsProvider(final JulieAdminClient adminClient) {
        this.adminClient = adminClient;
    }

    @Override
    public void createBindings(Set<JulieAclBinding> bindings) throws IOException {
        LOGGER.debug("AclsProvider: createBindings");
        List<AclBinding> bindingsAsNativeKafka =
                bindings.stream()
                        .filter(binding -> binding.asAclBinding().isPresent())
                        .map(binding -> binding.asAclBinding().get())
                        .collect(Collectors.toList());
        adminClient.createAcls(bindingsAsNativeKafka);
    }

    @Override
    public void clearBindings(Set<JulieAclBinding> bindings) throws IOException {
        LOGGER.debug("AclsProvider: clearAcls");
        for (JulieAclBinding binding : bindings) {
            try {
                adminClient.clearAcls(binding);
            } catch (IOException ex) {
                LOGGER.error(ex);
                throw ex;
            }
        }
    }

    @Override
    public Map<String, List<JulieAclBinding>> listAcls() {
        Map<String, List<JulieAclBinding>> map = new HashMap<>();
        adminClient
                .fetchAclsList()
                .forEach(
                        (topic, aclBindings) ->
                                map.put(
                                        topic,
                                        aclBindings.stream()
                                                .map(JulieAclBinding::new)
                                                .collect(Collectors.toList())));
        return map;
    }
}