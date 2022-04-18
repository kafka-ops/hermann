package com.purbon.kafka.julie;

import com.purbon.kafka.julie.model.JulieAclBinding;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AccessControlProvider {

    void createBindings(Set<JulieAclBinding> bindings) throws IOException;

    void clearBindings(Set<JulieAclBinding> bindings) throws IOException;

    default Map<String, List<JulieAclBinding>> listAcls() {
        return new HashMap<>();
    }
}
