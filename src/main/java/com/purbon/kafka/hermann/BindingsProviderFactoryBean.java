package com.purbon.kafka.hermann;

import com.purbon.kafka.julie.BindingsBuilderProvider;
import com.purbon.kafka.julie.api.JulieAdminClient;
import com.purbon.kafka.julie.roles.acls.AclsBindingsBuilder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class BindingsProviderFactoryBean implements FactoryBean<BindingsBuilderProvider> {

    public static final String ACCESS_CONTROL_DEFAULT_CLASS =
            "com.purbon.kafka.julie.roles.SimpleAclsProvider";

    private String accessControlProviderClass;
    private JulieAdminClient adminClient;

    private AppConfig appConfig;

    public BindingsProviderFactoryBean(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Override
    public BindingsBuilderProvider getObject() throws Exception {
        switch (accessControlProviderClass) {
            case ACCESS_CONTROL_DEFAULT_CLASS:
                var providerClass = configMap().get(ACCESS_CONTROL_DEFAULT_CLASS);
                Class<?> clazz = Class.forName(providerClass);
                Constructor<?> constructor = clazz.getConstructor(AppConfig.class);
                return (AclsBindingsBuilder) constructor.newInstance(appConfig);
            default:
                throw new IOException("Unknown access control provided. " + accessControlProviderClass);
        }
    }

    @Override
    public Class<?> getObjectType() {
        return BindingsBuilderProvider.class;
    }

    private Map<String, String> configMap() {
        var configMap = new HashMap<String, String>();
        configMap.put(ACCESS_CONTROL_DEFAULT_CLASS, AclsBindingsBuilder.class.getName());
        return configMap;
    }
}
