package com.purbon.kafka.hermann.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.purbon.kafka.hermann.model.HermannAclBinding;
import com.purbon.kafka.hermann.model.Topic;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AlterConfigOp;
import org.apache.kafka.clients.admin.AlterConfigOp.OpType;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.NewPartitions;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.acl.AccessControlEntryFilter;
import org.apache.kafka.common.acl.AclBinding;
import org.apache.kafka.common.acl.AclBindingFilter;
import org.apache.kafka.common.acl.AclOperation;
import org.apache.kafka.common.acl.AclPermissionType;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.config.ConfigResource.Type;
import org.apache.kafka.common.errors.InvalidConfigurationException;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.kafka.common.resource.PatternType;
import org.apache.kafka.common.resource.ResourcePatternFilter;
import org.apache.kafka.common.resource.ResourceType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class HermannAdminClient {

    private static final Logger LOGGER = LogManager.getLogger(HermannAdminClient.class);

    private AdminClient adminClient;

    public HermannAdminClient(AdminClient adminClient) {
        this.adminClient = adminClient;
    }

    public Set<String> listTopics(ListTopicsOptions options) throws IOException {
        Set<String> listOfTopics;
        try {
            listOfTopics = adminClient.listTopics(options).names().get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error(e);
            throw new IOException(e);
        }
        return listOfTopics;
    }


    public Set<String> listApplicationTopics() throws IOException {
        ListTopicsOptions options = new ListTopicsOptions();
        options.listInternal(false);
        return listTopics(options);
    }

    public void updateTopicConfig(Topic topic) throws IOException {
        try {
            updateTopicConfigPostAK23(topic);
        } catch (InterruptedException | ExecutionException ex) {
            LOGGER.error(ex);
            throw new IOException(ex);
        }
    }

    private void updateTopicConfigPostAK23(Topic topic)
            throws ExecutionException, InterruptedException {

        Config currentConfigs = getActualTopicConfig(topic.getName());

        Map<ConfigResource, Collection<AlterConfigOp>> configs = new HashMap<>();
        ArrayList<AlterConfigOp> listOfValues = new ArrayList<>();

        topic
                .getConfig()
                .forEach(
                        (configKey, configValue) -> {
                            listOfValues.add(
                                    new AlterConfigOp(new ConfigEntry(configKey, configValue), OpType.SET));
                        });
        Set<String> newEntryKeys = topic.getConfig().keySet();

        currentConfigs
                .entries()
                .forEach(
                        entry -> {
                            if (!entry.isDefault() && !newEntryKeys.contains(entry.name())) {
                                listOfValues.add(new AlterConfigOp(entry, OpType.DELETE));
                            }
                        });

        configs.put(new ConfigResource(Type.TOPIC, topic.getName()), listOfValues);

        adminClient.incrementalAlterConfigs(configs).all().get();
    }


    public int getPartitionCount(String topic) throws IOException {
        try {
            Map<String, TopicDescription> results =
                    adminClient.describeTopics(Collections.singletonList(topic)).all().get();
            return results.get(topic).partitions().size();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error(e);
            throw new IOException(e);
        }
    }

    public void updatePartitionCount(Topic topic, String topicName) throws IOException {
        Map<String, NewPartitions> map = new HashMap<>();
        map.put(topicName, NewPartitions.increaseTo(topic.getPartitions().intValue()));
        try {
            adminClient.createPartitions(map).all().get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error(e);
            throw new IOException(e);
        }
    }

    public void clearAcls() throws IOException {
        Collection<AclBindingFilter> filters = new ArrayList<>();
        filters.add(AclBindingFilter.ANY);
        clearAcls(filters);
    }

    public void clearAcls(HermannAclBinding aclBinding) throws IOException {
        Collection<AclBindingFilter> filters = new ArrayList<>();

        LOGGER.debug("clearAcl = " + aclBinding);
        ResourcePatternFilter resourceFilter =
                new ResourcePatternFilter(
                        ResourceType.valueOf(aclBinding.getResourceType()),
                        aclBinding.getResourceName(),
                        PatternType.valueOf(aclBinding.getPattern()));

        AccessControlEntryFilter accessControlEntryFilter =
                new AccessControlEntryFilter(
                        aclBinding.getPrincipal(),
                        aclBinding.getHost(),
                        AclOperation.valueOf(aclBinding.getOperation()),
                        AclPermissionType.ANY);

        AclBindingFilter filter = new AclBindingFilter(resourceFilter, accessControlEntryFilter);
        filters.add(filter);
        clearAcls(filters);
    }

    private void clearAcls(Collection<AclBindingFilter> filters) throws IOException {
        try {
            adminClient.deleteAcls(filters).all().get();
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error(e);
            throw new IOException(e);
        }
    }

    public Config getActualTopicConfig(String topic) {
        ConfigResource resource = new ConfigResource(Type.TOPIC, topic);
        Collection<ConfigResource> resources = Collections.singletonList(resource);

        Map<ConfigResource, Config> configs = null;
        try {
            configs = adminClient.describeConfigs(resources).all().get();
        } catch (InterruptedException | ExecutionException ex) {
            LOGGER.error(ex);
            throw new RuntimeException(ex);
        }

        return configs.get(resource);
    }

    public void createTopic(Topic topic) throws IOException {
        NewTopic newTopic =
                new NewTopic(topic.getName(), topic.getPartitions().intValue(), topic.getReplicationFactor())
                        .configs(topic.getConfig());
        try {
            createAllTopics(Collections.singleton(newTopic));
        } catch (ExecutionException | InterruptedException e) {
            if (e.getCause() instanceof TopicExistsException) {
                LOGGER.info(e.getMessage());
                return;
            }
            LOGGER.error(e);
            throw new IOException(e);
        }
    }

    private void createAllTopics(Collection<NewTopic> newTopics)
            throws ExecutionException, InterruptedException {
        adminClient.createTopics(newTopics).all().get();
    }

    public void deleteTopics(Collection<String> topics) throws IOException {
        try {
            adminClient.deleteTopics(topics).all().get();
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error(e);
            throw new IOException(e);
        }
    }

    public Map<String, Collection<AclBinding>> fetchAclsList() {
        Map<String, Collection<AclBinding>> acls = new HashMap<>();

        try {
            Collection<AclBinding> list = adminClient.describeAcls(AclBindingFilter.ANY).values().get();
            list.forEach(
                    aclBinding -> {
                        String name = aclBinding.pattern().name();
                        if (acls.get(name) == null) {
                            acls.put(name, new ArrayList<>());
                        }
                        Collection<AclBinding> updatedList = acls.get(name);
                        updatedList.add(aclBinding);
                        acls.put(name, updatedList);
                    });
        } catch (Exception e) {
            return new HashMap<>();
        }
        return acls;
    }

    public void createAcls(Collection<AclBinding> acls) {
        try {
            adminClient.createAcls(acls).all().get();
        } catch (InvalidConfigurationException ex) {
            LOGGER.error(ex);
            throw ex;
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error(e);
        }
    }

    public void close() {
        adminClient.close();
    }
}
