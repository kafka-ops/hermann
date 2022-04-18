package com.purbon.kafka.hermann.controller.request.acl;

import com.purbon.kafka.hermann.controller.request.AclSpec;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ConsumerSpec extends AclSpec {

    private String group = "*";
}
