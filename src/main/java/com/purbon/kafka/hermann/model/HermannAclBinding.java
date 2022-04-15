package com.purbon.kafka.hermann.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.common.acl.AccessControlEntry;
import org.apache.kafka.common.acl.AclBinding;
import org.apache.kafka.common.resource.ResourcePattern;
import org.apache.kafka.common.resource.ResourceType;

import java.util.Optional;

@Getter
@Setter
public class HermannAclBinding {

    @JsonIgnore
    private Optional<AclBinding> aclBindingOptional;

    private String resourceType;
    private String resourceName;
    private String host;
    private String operation;
    private String principal;
    private String pattern;


    public HermannAclBinding(
            String resourceType,
            String resourceName,
            String host,
            String operation,
            String principal,
            String pattern) {
        this.resourceType = resourceType;
        this.resourceName = resourceName;
        this.host = host;
        this.operation = operation;
        this.principal = principal;
        this.pattern = pattern;
        this.aclBindingOptional = Optional.empty();
    }

    public static HermannAclBinding build(
            String resourceTypeString,
            String resourceName,
            String host,
            String operation,
            String principal,
            String pattern) {

        ResourceType resourceType = ResourceType.valueOf(resourceTypeString);
        return new HermannAclBinding(
                resourceType.name(), resourceName, host, operation, principal, pattern);
    }

    public HermannAclBinding() {
        this(ResourceType.ANY.name(), "", "", "", "", "");
    }

    public HermannAclBinding(AclBinding binding) {

        this.aclBindingOptional = Optional.of(binding);

        AccessControlEntry entry = binding.entry();
        ResourcePattern pattern = binding.pattern();

        this.resourceType = pattern.resourceType().name();
        this.resourceName = pattern.name();
        this.principal = entry.principal();
        this.operation = entry.operation().name();
        this.pattern = pattern.patternType().name();
        this.host = entry.host();
    }

    public Optional<AclBinding> asAclBinding() {
        return aclBindingOptional;
    }
}
