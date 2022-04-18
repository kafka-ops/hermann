package com.purbon.kafka.julie;

import com.purbon.kafka.julie.model.Component;
import com.purbon.kafka.julie.model.JulieAclBinding;
import com.purbon.kafka.julie.model.JulieRoleAcl;
import com.purbon.kafka.julie.model.users.*;
import com.purbon.kafka.julie.model.users.platform.KsqlServerInstance;
import com.purbon.kafka.julie.model.users.platform.SchemaRegistryInstance;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface BindingsBuilderProvider {

    List<JulieAclBinding> buildBindingsForConnect(Connector connector, String topicPrefix);

    List<JulieAclBinding> buildBindingsForStreamsApp(
            String principal,
            String topicPrefix,
            List<String> readTopics,
            List<String> writeTopics,
            boolean eos);

    List<JulieAclBinding> buildBindingsForConsumers(
            Collection<Consumer> consumers, String resource, boolean prefixed);

    List<JulieAclBinding> buildBindingsForProducers(
            Collection<Producer> principals, String resource, boolean prefixed);

    default JulieAclBinding setPredefinedRole(
            String principal, String predefinedRole, String topicPrefix) {
        // NOOP
        return null;
    }

    List<JulieAclBinding> buildBindingsForSchemaRegistry(SchemaRegistryInstance schemaRegistry);

    List<JulieAclBinding> buildBindingsForControlCenter(String principal, String appId);

    default List<JulieAclBinding> setSchemaAuthorization(
            String principal, List<String> subjects, String role, boolean prefixed) {
        return Collections.emptyList();
    }

    default List<JulieAclBinding> setConnectorAuthorization(
            String principal, List<String> connectors) {
        return Collections.emptyList();
    }

    default List<JulieAclBinding> setClusterLevelRole(
            String role, String principal, Component component) throws IOException {
        return Collections.emptyList();
    }

    Collection<JulieAclBinding> buildBindingsForKSqlServer(KsqlServerInstance ksqlServer);

    Collection<JulieAclBinding> buildBindingsForKSqlApp(KSqlApp app, String prefix);

    Collection<JulieAclBinding> buildBindingsForJulieRole(
            Other other, String name, List<JulieRoleAcl> acls) throws IOException;
}