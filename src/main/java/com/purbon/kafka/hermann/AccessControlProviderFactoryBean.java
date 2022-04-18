package com.purbon.kafka.hermann;

import com.purbon.kafka.julie.AccessControlProvider;
import com.purbon.kafka.julie.api.JulieAdminClient;
import com.purbon.kafka.julie.roles.SimpleAclsProvider;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;

import java.io.IOException;
import java.lang.reflect.Constructor;

@Setter
@Getter
public class AccessControlProviderFactoryBean implements FactoryBean<AccessControlProvider> {

    public static final String ACCESS_CONTROL_DEFAULT_CLASS =
            "com.purbon.kafka.julie.roles.SimpleAclsProvider";

    private String accessControlProviderClass;

    private JulieAdminClient adminClient;

    public AccessControlProviderFactoryBean(JulieAdminClient adminClient) {
        this.adminClient = adminClient;
    }

    @Override
    public AccessControlProvider getObject() throws Exception {
        Class<?> clazz = Class.forName(accessControlProviderClass);

        switch (accessControlProviderClass) {
            case ACCESS_CONTROL_DEFAULT_CLASS:
                Constructor<?> constructor = clazz.getConstructor(JulieAdminClient.class);
                return (SimpleAclsProvider) constructor.newInstance(adminClient);
            default:
                throw new IOException("Unknown access control provided. " + accessControlProviderClass);
        }
    }

    @Override
    public Class<?> getObjectType() {
        return AccessControlProvider.class;
    }
}
