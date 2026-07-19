package com.aipe.observation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Map;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ResourceReference implements Serializable {
    private static final long serialVersionUID = 1L;
    private String resourceId;
    private String resourceType;
    private String host;
    private String port;
    private Map<String, String> labels;
}
