package com.purbon.kafka.hermann.controller.response;

import com.purbon.kafka.julie.model.JulieAclBinding;
import com.purbon.kafka.julie.model.User;
import lombok.Data;

import java.util.List;

@Data
public class AclResponse<U extends User> {

    private List<JulieAclBinding> bindings;
    private List<U> entity;

    public AclResponse(List<JulieAclBinding> bindings, List<U> entity) {
        this.bindings = bindings;
        this.entity = entity;
    }
}
