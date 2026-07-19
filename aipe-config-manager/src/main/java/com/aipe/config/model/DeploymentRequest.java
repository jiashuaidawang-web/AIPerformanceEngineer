package com.aipe.config.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Map;

@Data @NoArgsConstructor @AllArgsConstructor
public class DeploymentRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String agentType;
    private String target;
    private String version;
    private Map<String, String> properties;
}
